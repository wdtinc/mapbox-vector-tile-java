package com.wdtinc.mapbox_vector_tile.adapt.jts;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.wdtinc.mapbox_vector_tile.*;
import com.wdtinc.mapbox_vector_tile.MvtUtil;
import com.wdtinc.mapbox_vector_tile.encoding.GeomCmd;
import com.wdtinc.mapbox_vector_tile.encoding.ZigZag;
import com.wdtinc.mapbox_vector_tile.util.Vec2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Adapt JTS {@link Geometry} to 'Mapbox Vector Tile' objects.
 */
public final class JtsAdapter {

    /**
     * Create geometry clipped and then normalized to tile 'pixel' dimensions declared in {@code mvtParams}.
     *
     * @param g original 'source' geometry
     * @param tileEnvelope bounds for tile
     * @param geomFactory creates a geometry for the tile envelope
     * @param mvtParams specifies vector tile properties
     * @return clipped original geometry to the tile extents
     */
    public static Geometry createTileGeom(Geometry g, Envelope tileEnvelope, GeometryFactory geomFactory, MvtParams mvtParams) {
        final Geometry tileEnvelopeGeom = geomFactory.toGeometry(tileEnvelope);

        final AffineTransformation t = new AffineTransformation();
        final double xDiff = tileEnvelope.getWidth();
        final double yDiff = tileEnvelope.getHeight();

        final double xOffset = -tileEnvelope.getMinX();
        final double yOffset = -tileEnvelope.getMinY();

        t.translate(xOffset, yOffset);
        t.scale(1d / (xDiff / (double)mvtParams.tileSize), 1d / (yDiff / (double)mvtParams.tileSize));

        // The area contained in BOTH the 'original geometry', g, AND the 'tile envelope geometry' is the 'tile geometry'
        return t.transform(tileEnvelopeGeom.intersection(g));
    }

    /**
     * Get the MVT type mapping for the provided JTS Geometry.
     *
     * @param geometry JTS Geometry to get type for
     * @return MVT type for the given JTS Geometry, may return
     *     {@link com.wdtinc.mapbox_vector_tile.VectorTile.Tile.GeomType#UNKNOWN}
     */
    public static VectorTile.Tile.GeomType toGeomType(Geometry geometry) {
        VectorTile.Tile.GeomType result = VectorTile.Tile.GeomType.UNKNOWN;

        if(geometry instanceof Point
                || geometry instanceof MultiPoint) {
            result = VectorTile.Tile.GeomType.POINT;

        } else if(geometry instanceof LineString
                || geometry instanceof MultiLineString) {
            result = VectorTile.Tile.GeomType.LINESTRING;

        } else if(geometry instanceof Polygon
                || geometry instanceof MultiPolygon) {
            result = VectorTile.Tile.GeomType.POLYGON;
        }

        return result;
    }

    /**
     * <p>Recursively convert a {@link Geometry}, which may be an instance of {@link GeometryCollection} with mixed
     * element types, into a flat list containing only the following {@link Geometry} types:</p>
     * <ul>
     *     <li>{@link Point}</li>
     *     <li>{@link LineString}</li>
     *     <li>{@link Polygon}</li>
     *     <li>{@link MultiPoint}</li>
     *     <li>{@link MultiLineString}</li>
     *     <li>{@link MultiPolygon}</li>
     * </ul>
     * <p>WARNING: Any other Geometry types that were not mentioned in the list above will be discarded!</p>
     * <p>Useful for converting a generic geometry into a list of simple MVT-feature-ready geometries.</p>
     *
     * @param geom geometry to flatten
     * @return list of MVT-feature-ready geometries
     */
    private static List<Geometry> flatFeatureList(Geometry geom) {
        final List<Geometry> singleGeoms = new ArrayList<>();
        final Stack<Geometry> geomStack = new Stack<>();

        Geometry nextGeom;
        int nextGeomCount;

        geomStack.push(geom);
        while(!geomStack.isEmpty()) {
            nextGeom = geomStack.pop();

            if(nextGeom instanceof Point
                    || nextGeom instanceof MultiPoint
                    || nextGeom instanceof LineString
                    || nextGeom instanceof MultiLineString
                    || nextGeom instanceof Polygon
                    || nextGeom instanceof MultiPolygon) {

                singleGeoms.add(nextGeom);

            } else if(nextGeom instanceof GeometryCollection) {

                // Push all child geometries
                nextGeomCount = geom.getNumGeometries();
                for(int i = 0; i < nextGeomCount; ++i) {
                    geomStack.push(nextGeom.getGeometryN(i));
                }

            }
        }

        return singleGeoms;
    }

    // TODO: Support layer tags (feature attributes)

    /**
     * <p>Convert JTS {@link Geometry} to a list of vector tile features.
     * The Geometry must be in 'pixel' space where (0, 0) is the lower left and (256, 256)
     * is the upper right.</p>
     *
     * <p>Each geometry will have its own ID.</p>
     *
     * @param geometry JTS geometry to convert
     * @param cursor MVT layer cursor
     * @param mvtParams specifies vector tile properties
     * @param filter determines which geometry to accept or reject for inclusion in the feature
     */
    public static List<VectorTile.Tile.Feature> toFeatures(Geometry geometry, Vec2d cursor, MvtParams mvtParams,
                                                           IGeometryFilter filter) {

        // Guard: empty geometry
        if(geometry.isEmpty()) {
            return Collections.emptyList();
        }


        final List<VectorTile.Tile.Feature> features = new ArrayList<>();
        final List<Geometry> flatGeomList = flatFeatureList(geometry);

        int nextFeatureId = 1;
        VectorTile.Tile.Feature nextFeature;

        for(Geometry nextGeom : flatGeomList) {

            if(!filter.accept(nextGeom)) {
                continue;
            }

            nextFeature = toFeature(nextGeom, cursor, mvtParams, nextFeatureId++);
            if(nextFeature != null) {
                features.add(nextFeature);
            }
        }

        return features;
    }

    /**
     * Create and return a feature from a geometry. Returns null on failure.
     *
     * @param geom flat geometry via {@link #flatFeatureList(Geometry)} that can be translated to a feature
     * @param cursor vector tile cursor position
     * @param mvtParams parameters for vector tile creation
     * @param featureId id value to apply to the feature
     * @return new tile feature instance, or null on failure
     */
    private static VectorTile.Tile.Feature toFeature(Geometry geom, Vec2d cursor, MvtParams mvtParams,
                                                     int featureId) {

        // Guard: UNKNOWN Geometry
        final VectorTile.Tile.GeomType mvtGeomType = JtsAdapter.toGeomType(geom);
        if(mvtGeomType == VectorTile.Tile.GeomType.UNKNOWN) {
            return null;
        }


        final VectorTile.Tile.Feature.Builder featureBuilder = VectorTile.Tile.Feature.newBuilder();
        final boolean mvtClosePath = MvtUtil.shouldClosePath(mvtGeomType);
        final List<Integer> mvtGeom = new ArrayList<>();

        featureBuilder.setId(featureId);
        featureBuilder.setType(mvtGeomType);

        if(geom instanceof Point || geom instanceof MultiPoint) {

            // Encode as MVT point or multipoint
            mvtGeom.addAll(ptsToGeomCmds(geom, cursor, mvtParams));

        } else if(geom instanceof LineString || geom instanceof MultiLineString) {

            // Encode as MVT linestring or multi-linestring
            for (int i = 0; i < geom.getNumGeometries(); ++i) {
                mvtGeom.addAll(linesToGeomCmds(geom.getGeometryN(i), mvtClosePath, cursor, mvtParams));
            }

        } else if(geom instanceof MultiPolygon || geom instanceof Polygon) {

            for(int i = 0; i < geom.getNumGeometries(); ++i) {

                // Encode as MVT polygon or multi-polygon
                final Polygon nextPoly = (Polygon) geom.getGeometryN(i);


                // Add exterior ring
                // TODO: UNCHECKED ENFORCE CCW WINDING (positive area)... JTS Normal form is SUPPOSED TO obey
                mvtGeom.addAll(linesToGeomCmds(nextPoly.getExteriorRing(), mvtClosePath, cursor, mvtParams));

                // Add interior rings
                // TODO: UNCHECKED ENFORCE CW WINDING (negative area)... JTS Normal form is SUPPOSED TO obey
                for(int ringIndex = 0; ringIndex < nextPoly.getNumInteriorRing(); ++ringIndex) {
                    mvtGeom.addAll(linesToGeomCmds(nextPoly.getInteriorRingN(ringIndex), mvtClosePath, cursor, mvtParams));
                }
            }
        }

        featureBuilder.addAllGeometry(mvtGeom);

        return featureBuilder.build();
    }


    /**
     * <p>Convert a {@link Point} or {@link MultiPoint} geometry to a list of MVT geometry drawing commands. See
     * <a href="https://github.com/mapbox/vector-tile-spec">vector-tile-spec</a>
     * for details.</p>
     *
     * <p>WARNING: The value of the {@code cursor} parameter is modified as a result of calling this method.</p>
     *
     * @param geom input of type {@link Point} or {@link MultiPoint}. Type is NOT checked and expected to be correct.
     * @param cursor modified during processing to contain next MVT cursor position
     * @param mvtParams parameters for MVT creation
     * @return list of commands
     */
    private static List<Integer> ptsToGeomCmds(final Geometry geom, final Vec2d cursor, final MvtParams mvtParams) {

        // Guard: empty geometry coordinates
        final Coordinate[] geomCoords = geom.getCoordinates();
        if(geomCoords.length <= 0) {
            Collections.emptyList();
        }


        /** Tile commands and parameters */
        final List<Integer> geomCmds = new ArrayList<>(geomCmdBuffLenPts(geomCoords.length));

        /** Holds next MVT coordinate */
        final Vec2d mvtPos = new Vec2d();

        /** Holds next tile coordinate */
        final Vec2d tilePos = new Vec2d();

        // TODO: Validate 'MoveTo' values do not contain duplicates, are less than max command length

        /** Length of 'MoveTo' draw command */
        int moveCmdLen = 0;

        // Insert placeholder for 'MoveTo' command header
        geomCmds.add(0);

        Coordinate nextCoord;

        for(int i = 0; i < geomCoords.length; ++i) {
            nextCoord = geomCoords[i];

            // Tile 'pixel' coord to MVT coord
            MvtUtil.toMvtCoord(tilePos.set(nextCoord.x, nextCoord.y), mvtParams, mvtPos);

            // Ignore duplicate MVT points
            if(i == 0 || !equalAsInts(cursor, mvtPos)) {
                ++moveCmdLen;
                moveCursor(cursor, geomCmds, mvtPos);
            }
        }

        // Write 'MoveTo' command header to first index
        geomCmds.set(0, GeomCmd.cmdHdr(Command.MoveTo, moveCmdLen));

        return geomCmds;
    }


    /**
     * <p>Convert a {@link LineString} or {@link Polygon} to a list of MVT geometry drawing commands.
     * A {@link MultiLineString} or {@link MultiPolygon} can be encoded by calling this method multiple times.</p>
     *
     * <p>See <a href="https://github.com/mapbox/vector-tile-spec">vector-tile-spec</a> for details.</p>
     *
     * <p>WARNING: The value of the {@code cursor} parameter is modified as a result of calling this method.</p>
     *
     * @param geom input of type {@link LineString} or {@link Polygon}. Type is NOT checked and expected to be correct.
     * @param closeEnabled whether a 'ClosePath' command should terminate the command list
     * @param mvtParams parameters for MVT creation
     * @param cursor modified during processing to contain next MVT cursor position
     * @return list of commands
     */
    private static List<Integer> linesToGeomCmds(
            final Geometry geom,
            final boolean closeEnabled,
            final Vec2d cursor,
            final MvtParams mvtParams) {

        // Guard: Not enough geometry coordinates for a line
        final Coordinate[] geomCoords = geom.getCoordinates();
        if(geomCoords.length < 2) {
            Collections.emptyList();
        }


        /** Tile commands and parameters */
        final List<Integer> geomCmds = new ArrayList<>(geomCmdBuffLenLines(geomCoords.length, closeEnabled));

        /** Holds next MVT coordinate */
        final Vec2d mvtPos = new Vec2d();

        /** Holds next tile coordinate */
        final Vec2d tilePos = new Vec2d();

        // Initial coordinate
        Coordinate nextCoord = geomCoords[0];

        // Encode initial 'MoveTo' command
        geomCmds.add(GeomCmd.cmdHdr(Command.MoveTo, 1));

        // Tile 'pixel' coord to MVT coord
        MvtUtil.toMvtCoord(tilePos.set(nextCoord.x, nextCoord.y), mvtParams, mvtPos);

        moveCursor(cursor, geomCmds, mvtPos);


        /** Index of 'LineTo' 'command header' */
        final int lineToCmdHdrIndex = geomCmds.size();

        // Insert placeholder for 'LineTo' command header
        geomCmds.add(0);


        // TODO: Validate 'LineTo' values are less than max command length

        /** Length of 'LineTo' draw command */
        int lineToLength = 0;


        for(int i = 1; i < geomCoords.length - 1; ++i) {
            nextCoord = geomCoords[i];

            // Tile 'pixel' coord to MVT coord
            MvtUtil.toMvtCoord(tilePos.set(nextCoord.x, nextCoord.y), mvtParams, mvtPos);

            // Ignore duplicate MVT points in sequence
            if(!equalAsInts(cursor, mvtPos)) {
                ++lineToLength;
                moveCursor(cursor, geomCmds, mvtPos);
            }
        }


        // Final coordinate
        nextCoord = geomCoords[geomCoords.length - 1];

        // Tile 'pixel' coord to MVT coord
        MvtUtil.toMvtCoord(tilePos.set(nextCoord.x, nextCoord.y), mvtParams, mvtPos);

        // Ignore duplicate MVT points
        // Ignore final coordinates equivalent to a 'ClosePath'
        if(!equalAsInts(cursor, mvtPos) && (!closeEnabled || !geomCoords[0].equals(nextCoord))) {
            ++lineToLength;
            moveCursor(cursor, geomCmds, mvtPos);
        }


        if(lineToLength > 0) {

            // Write 'LineTo' 'command header'
            geomCmds.set(lineToCmdHdrIndex, GeomCmd.cmdHdr(Command.LineTo, lineToLength));

            if(closeEnabled) {
                geomCmds.add(GeomCmd.closePathCmdHdr());
            }

            return geomCmds;

        } else {

            // Invalid geometry, need at least 1 'LineTo' value to make a Multiline or Polygon
            return Collections.emptyList();
        }
    }

    /**
     * <p>Appends {@link ZigZag#encode(int)} of delta in x,y from {@code cursor} to {@code mvtPos} into the {@code geomCmds} buffer.</p>
     *
     * <p>Afterwards, the {@code cursor} values are changed to match the {@code mvtPos} values.</p>
     *
     * @param cursor MVT cursor position
     * @param geomCmds geometry command list
     * @param mvtPos next MVT cursor position
     */
    private static void moveCursor(Vec2d cursor, List<Integer> geomCmds, Vec2d mvtPos) {

        // Delta, then zigzag
        geomCmds.add(ZigZag.encode((int)mvtPos.x - (int)cursor.x));
        geomCmds.add(ZigZag.encode((int)mvtPos.y - (int)cursor.y));

        cursor.set(mvtPos);
    }

    /**
     * Return true if the values of the two vectors are equal when cast as ints.
     *
     * @param a first vector to compare
     * @param b second vector to compare
     * @return true if the values of the two vectors are equal when cast as ints
     */
    private static boolean equalAsInts(Vec2d a, Vec2d b) {
        return (int) b.x == (int) a.x && (int) b.y == (int) a.y;
    }

    /**
     * Get required geometry buffer size for a {@link Point} or {@link MultiPoint} geometry.
     *
     * @param coordCount coordinate count for the geometry
     * @return required geometry buffer length
     */
    public static int geomCmdBuffLenPts(int coordCount) {

        // 1 MoveTo Header, 2 parameters * coordCount
        return 1 + (coordCount * 2);
    }

    /**
     * Get required geometry buffer size for a {@link LineString} or {@link Polygon} geometry.
     *
     * @param coordCount coordinate count for the geometry
     * @param closeEnabled whether a 'ClosePath' command should terminate the command list
     * @return required geometry buffer length
     */
    public static int geomCmdBuffLenLines(int coordCount, boolean closeEnabled) {

        // MoveTo Header, LineTo Header, Optional ClosePath Header, 2 parameters * coordCount
        return 2 + (closeEnabled ? 1 : 0) + (coordCount * 2);
    }
}
