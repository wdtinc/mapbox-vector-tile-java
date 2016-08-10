package com.wdtinc.mapbox_vector_tile;

import com.vividsolutions.jts.geom.*;
import com.wdtinc.mapbox_vector_tile.adapt.jts.*;
import com.wdtinc.mapbox_vector_tile.build.MvtLayerBuild;
import com.wdtinc.mapbox_vector_tile.build.MvtLayerParams;
import com.wdtinc.mapbox_vector_tile.build.MvtLayerProps;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Test building MVTs.
 */
public final class MvtBuildTest {

    /** Fixed randomization with arbitrary seed value */
    private static final long SEED = 487125064L;

    /** Example world is 100x100 box */
    private static final double WORLD_SIZE = 100D;

    @Test
    public void testPoints() throws IOException {
        final Random random = new Random(SEED);

        // Create input geometry
        final GeometryFactory geomFactory = new GeometryFactory();
        final MultiPoint multiPointGeom = buildMultiPoint(random, 200, geomFactory);

        // Build MVT
        final VectorTile.Tile.Builder tileBuilder = VectorTile.Tile.newBuilder();
        final MvtLayerParams mvtParams = new MvtLayerParams();
        final IGeometryFilter acceptAllGeomFilter = geometry -> true;

        // Build tile envelope - 1 quadrant of the world
        final Envelope tileEnvelope = new Envelope(0d, WORLD_SIZE * .5d, 0d, WORLD_SIZE * .5d);

        // Build MVT tile geometry
        final TileGeomResult tileGeom = JtsAdapter.createTileGeom(multiPointGeom, tileEnvelope, geomFactory,
                mvtParams, acceptAllGeomFilter);

        // Create MVT layer
        final VectorTile.Tile.Layer.Builder layerBuilder = MvtLayerBuild.newLayerBuilder("layerNameHere", mvtParams);
        final MvtLayerProps layerProps = new MvtLayerProps();
        final UserDataIgnoreConverter ignoreUserData = new UserDataIgnoreConverter();

        // MVT tile geometry to MVT features
        final List<VectorTile.Tile.Feature> features = JtsAdapter.toFeatures(tileGeom.mvtGeoms, layerProps, ignoreUserData);
        layerBuilder.addAllFeatures(features);
        MvtLayerBuild.writeProps(layerBuilder, layerProps);

        // Build MVT layer
        final VectorTile.Tile.Layer layer = layerBuilder.build();

        // Add built layer to MVT
        tileBuilder.addLayers(layer);

        /// Build MVT
        final VectorTile.Tile mvt = tileBuilder.build();

        // MVT Bytes
        final byte[] bytes = mvt.toByteArray();

        assertNotNull(bytes);

        // Load multipolygon z0 tile
        final List<Geometry> geoms = MvtReader.loadMvt(
                new ByteArrayInputStream(bytes),
                new GeometryFactory(),
                new TagKeyValueMapConverter());

        // Check that MVT geometries are the same as the ones that were encoded above
        assertEquals(geoms, tileGeom.mvtGeoms);
    }

    private static MultiPoint buildMultiPoint(Random random, int pointCount, GeometryFactory geomFactory) {
        final CoordinateSequence coordSeq = geomFactory.getCoordinateSequenceFactory().create(pointCount, 2);
        for(int i = 0; i < pointCount; ++i) {
            final Coordinate coord = coordSeq.getCoordinate(i);
            coord.setOrdinate(0, random.nextDouble() * WORLD_SIZE);
            coord.setOrdinate(1, random.nextDouble() * WORLD_SIZE);
        }
        return geomFactory.createMultiPoint(coordSeq);
    }
}
