package com.wdtinc.mapbox_vector_tile;

import com.vividsolutions.jts.geom.*;
import com.wdtinc.mapbox_vector_tile.adapt.jts.MvtReader;
import com.wdtinc.mapbox_vector_tile.adapt.jts.TagKeyValueMapConverter;
import com.wdtinc.mapbox_vector_tile.util.JtsGeomStats;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test reading MVTs.
 */
public final class MvtReaderTest {

    @Test
    public void simpleTest() {
        try {

            // Load multipolygon z0 tile
            final List<Geometry> geoms = loadGeoms("src/test/resources/vec_tile_test/0/0/0.mvt");

            // Debug stats of multipolygon
            final JtsGeomStats stats = JtsGeomStats.getStats(geoms);
            LoggerFactory.getLogger(MvtReaderTest.class).info("Stats: {}", stats);

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testNegExtPolyRings() {
        try {

            // Single MultiPolygon with two triangles that have negative area from shoelace formula
            // Support for 'V1' MVTs.
            final List<Geometry> geoms = loadGeoms(
                    "src/test/resources/mapbox/vector_tile_js/multi_poly_neg_exters.mvt",
                    MvtReader.RING_CLASSIFIER_V1);

            assertEquals(1, geoms.size());
            assertTrue(geoms.get(0) instanceof MultiPolygon);

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private static List<Geometry> loadGeoms(String path) throws IOException {
        return MvtReader.loadMvt(
                Paths.get(path),
                new GeometryFactory(),
                new TagKeyValueMapConverter());
    }

    private static List<Geometry> loadGeoms(String path,
                                            MvtReader.RingClassifier ringClassifier) throws IOException {
        return MvtReader.loadMvt(
                Paths.get(path),
                new GeometryFactory(),
                new TagKeyValueMapConverter(),
                ringClassifier);
    }
}
