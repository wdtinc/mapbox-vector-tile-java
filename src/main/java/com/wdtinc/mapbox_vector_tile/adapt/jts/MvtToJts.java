package com.wdtinc.mapbox_vector_tile.adapt.jts;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.*;
import com.wdtinc.mapbox_vector_tile.Command;
import com.wdtinc.mapbox_vector_tile.VectorTile;
import com.wdtinc.mapbox_vector_tile.encoding.GeomCmd;
import com.wdtinc.mapbox_vector_tile.encoding.ZigZag;
import com.wdtinc.mapbox_vector_tile.util.Vec2d;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class MvtToJts {
    private static final int MIN_LINE_STRING_LEN = 6; // MoveTo,1 + LineTo,1
    private static final int MIN_POLYGON_LEN = 9; // MoveTo,1 + LineTo,2 + ClosePath

    public static List<Geometry> loadMvt(Path p, GeometryFactory geomFactory) throws IOException {
        final List<Geometry> tileGeoms = new ArrayList<>();
        final VectorTile.Tile mvt = VectorTile.Tile.parseFrom(new FileInputStream(p.toFile()));
        Vec2d cursor = new Vec2d();

        for(VectorTile.Tile.Layer nextLayer : mvt.getLayersList()) {
            for(VectorTile.Tile.Feature nextFeature : nextLayer.getFeaturesList()) {

                final long id = nextFeature.getId(); // TODO: ID --> User data
                final VectorTile.Tile.GeomType geomType = nextFeature.getType();

                if(geomType == VectorTile.Tile.GeomType.UNKNOWN) {
                    continue;
                }

                final List<Integer> geomCmds = nextFeature.getGeometryList();
                final Geometry nextGeom = readGeometry(geomCmds, geomType, geomFactory, cursor);
                if(nextGeom != null) {
                    tileGeoms.add(nextGeom);
                }
            }
        }

        return tileGeoms;
    }

    private static Geometry readGeometry(List<Integer> geomCmds, VectorTile.Tile.GeomType geomType,
                             GeometryFactory geomFactory, Vec2d cursor) {
        Geometry result = null;

        switch(geomType) {
            case POINT:
                result = readPoints(geomFactory, geomCmds, cursor);
                break;
            case LINESTRING:
                result = readLines(geomFactory, geomCmds, cursor);
                break;
            case POLYGON:
                result = readPolys(geomFactory, geomCmds, cursor);
                break;
            default:
                // TODO: error, unhandled geometry type
        }

        return result;
    }

    private static Geometry readPoints(GeometryFactory geomFactory, List<Integer> geomCmds, Vec2d cursor) {

        // Guard: must have header
        if(geomCmds.isEmpty()) {
            return null;
        }

        // Read command header
        final int cmdHdr = geomCmds.get(0);
        final int cmdLength = GeomCmd.getCmdLength(cmdHdr);
        final Command cmd = GeomCmd.getCmd(cmdHdr);

        // Guard: command type
        if(cmd != Command.MoveTo) {
            return null;
        }

        // Guard: minimum command length
        if(cmdLength < 1) {
            return null;
        }

        // Guard: header data unsupported by geometry command buffer
        //  (require header and at least 1 value * 2 params)
        if(cmdLength * Command.MoveTo.getParamCount() + 1 > geomCmds.size()) {
            return null;
        }

        final CoordinateSequence coordSeq = geomFactory.getCoordinateSequenceFactory().create(cmdLength, 2);
        Coordinate nextCoord;

        for(int i = 0; i < cmdLength; ++i) {
            cursor.add(
                    ZigZag.decode(geomCmds.get(i * 2)),
                    ZigZag.decode(geomCmds.get(i * 2 + 1))
            );

            nextCoord = coordSeq.getCoordinate(i);
            nextCoord.setOrdinate(0, cursor.x);
            nextCoord.setOrdinate(1, cursor.y);
        }

        return coordSeq.size() == 1 ? geomFactory.createPoint(coordSeq) : geomFactory.createMultiPoint(coordSeq);
    }

    private static Geometry readLines(GeometryFactory geomFactory, List<Integer> geomCmds, Vec2d cursor) {

        // Guard: must have header
        if(geomCmds.isEmpty()) {
            return null;
        }

        /** Geometry command index */
        int i = 0;

        int cmdHdr;
        int cmdLength;
        Command cmd;
        List<LineString> geoms = new ArrayList<>(1);
        CoordinateSequence nextCoordSeq;
        Coordinate nextCoord;

        while(i <= geomCmds.size() - MIN_LINE_STRING_LEN) {

            // --------------------------------------------
            // Expected: MoveTo command of length 1
            // --------------------------------------------

            // Read command header
            cmdHdr = geomCmds.get(i++);
            cmdLength = GeomCmd.getCmdLength(cmdHdr);
            cmd = GeomCmd.getCmd(cmdHdr);

            // Guard: command type and length
            if(cmd != Command.MoveTo || cmdLength != 1) {
                break;
            }

            // Update cursor position with relative move
            cursor.add(
                    ZigZag.decode(geomCmds.get(i++)),
                    ZigZag.decode(geomCmds.get(i++))
            );


            // --------------------------------------------
            // Expected: LineTo command of length > 0
            // --------------------------------------------

            // Read command header
            cmdHdr = geomCmds.get(i++);
            cmdLength = GeomCmd.getCmdLength(cmdHdr);
            cmd = GeomCmd.getCmd(cmdHdr);

            // Guard: command type and length
            if(cmd != Command.LineTo || cmdLength < 1) {
                break;
            }

            // Guard: header data length unsupported by geometry command buffer
            //  (require at least (1 value * 2 params) + current_index)
            if((cmdLength * Command.LineTo.getParamCount()) + i > geomCmds.size()) {
                break;
            }

            nextCoordSeq = geomFactory.getCoordinateSequenceFactory().create(1 + cmdLength, 2);

            // Set first point from MoveTo command
            nextCoord = nextCoordSeq.getCoordinate(0);
            nextCoord.setOrdinate(0, cursor.x);
            nextCoord.setOrdinate(1, cursor.y);

            // Set remaining points from LineTo command
            for(int lineToIndex = 0; lineToIndex < cmdLength; ++lineToIndex) {

                // Update cursor position with relative line delta
                cursor.add(
                        ZigZag.decode(geomCmds.get(i++)),
                        ZigZag.decode(geomCmds.get(i++))
                );

                nextCoord = nextCoordSeq.getCoordinate(lineToIndex + 1);
                nextCoord.setOrdinate(0, cursor.x);
                nextCoord.setOrdinate(1, cursor.y);
            }

            geoms.add(geomFactory.createLineString(nextCoordSeq));
        }

        return geoms.size() == 1 ? geoms.get(0) : geomFactory.createMultiLineString((LineString[]) geoms.toArray());
    }

    private static Geometry readPolys(GeometryFactory geomFactory, List<Integer> geomCmds, Vec2d cursor) {

        // Guard: must have header
        if(geomCmds.isEmpty()) {
            return null;
        }

        /** Geometry command index */
        int i = 0;

        int cmdHdr;
        int cmdLength;
        Command cmd;
        List<Polygon> rings = new ArrayList<>(1);
        CoordinateSequence nextCoordSeq;
        Coordinate nextCoord;

        while(i <= geomCmds.size() - MIN_POLYGON_LEN) {

            // --------------------------------------------
            // Expected: MoveTo command of length 1
            // --------------------------------------------

            // Read command header
            cmdHdr = geomCmds.get(i++);
            cmdLength = GeomCmd.getCmdLength(cmdHdr);
            cmd = GeomCmd.getCmd(cmdHdr);

            // Guard: command type and length
            if(cmd != Command.MoveTo || cmdLength != 1) {
                break;
            }

            // Update cursor position with relative move
            cursor.add(
                    ZigZag.decode(geomCmds.get(i++)),
                    ZigZag.decode(geomCmds.get(i++))
            );


            // --------------------------------------------
            // Expected: LineTo command of length > 1
            // --------------------------------------------

            // Read command header
            cmdHdr = geomCmds.get(i++);
            cmdLength = GeomCmd.getCmdLength(cmdHdr);
            cmd = GeomCmd.getCmd(cmdHdr);

            // Guard: command type and length
            if(cmd != Command.LineTo || cmdLength < 2) {
                break;
            }

            // Guard: header data length unsupported by geometry command buffer
            //  (require at least (2 values * 2 params) + (current index 'i') + (1 for ClosePath))
            if((cmdLength * Command.LineTo.getParamCount()) + i + 1 > geomCmds.size()) {
                break;
            }

            nextCoordSeq = geomFactory.getCoordinateSequenceFactory().create(2 + cmdLength, 2);

            // Set first point from MoveTo command
            nextCoord = nextCoordSeq.getCoordinate(0);
            nextCoord.setOrdinate(0, cursor.x);
            nextCoord.setOrdinate(1, cursor.y);

            // Set remaining points from LineTo command
            for(int lineToIndex = 0; lineToIndex < cmdLength; ++lineToIndex) {

                // Update cursor position with relative line delta
                cursor.add(
                        ZigZag.decode(geomCmds.get(i++)),
                        ZigZag.decode(geomCmds.get(i++))
                );

                nextCoord = nextCoordSeq.getCoordinate(lineToIndex + 1);
                nextCoord.setOrdinate(0, cursor.x);
                nextCoord.setOrdinate(1, cursor.y);
            }


            // --------------------------------------------
            // Expected: ClosePath command of length 0
            // --------------------------------------------

            // Read command header
            cmdHdr = geomCmds.get(i++);
            cmdLength = GeomCmd.getCmdLength(cmdHdr);
            cmd = GeomCmd.getCmd(cmdHdr);

            if(cmd != Command.ClosePath || cmdLength != 1) {
                break;
            }

            // Set last point from ClosePath command
            nextCoord = nextCoordSeq.getCoordinate(nextCoordSeq.size() - 1);
            nextCoord.setOrdinate(0, nextCoordSeq.getOrdinate(0, 0));
            nextCoord.setOrdinate(1, nextCoordSeq.getOrdinate(0, 1));

            rings.add(geomFactory.createPolygon(nextCoordSeq));
        }


        // Classify rings
        final List<Polygon> polygons = classifyRings(rings, geomFactory);
        if(polygons.size() < 1) {
            return null;

        } else if(polygons.size() == 1) {
            return polygons.get(0);

        } else {
            return geomFactory.createMultiPolygon((Polygon[]) polygons.toArray());
        }
    }


    private static List<Polygon> classifyRings(List<Polygon> rings, GeometryFactory geomFactory) {
        final List<Polygon> polygons = new ArrayList<>();
        final List<Polygon> holes = new ArrayList<>();

        double outerArea = 0d;
        Polygon outerPoly = null;

        for(Polygon r : rings) {
            double area = CGAlgorithms.signedArea(r.getCoordinates());

            if(!r.getExteriorRing().isRing()) {
                continue; // sanity check, could probably be handled in a isSimple() check
            }

            if(area == 0d) {
                continue; // zero-area
            }

            if(area > 0d) {
                if(outerPoly != null) {
                    polygons.add(geomFactory.createPolygon((LinearRing)outerPoly.getExteriorRing(), (LinearRing[]) holes.toArray()));
                    holes.clear();
                }

                // Pos --> CCW, Outer
                outerPoly = r;
                outerArea = area;

            } else {

                if(Math.abs(outerArea) < Math.abs(area)) {
                    continue; // Holes must have less area, could probably be handled in a isSimple() check
                }

                // Neg --> CW, Hole
                holes.add(r);
            }
        }

        if(outerPoly != null) {
            polygons.add(geomFactory.createPolygon((LinearRing)outerPoly.getExteriorRing(), (LinearRing[]) holes.toArray()));
        }

        return polygons;
    }
}
