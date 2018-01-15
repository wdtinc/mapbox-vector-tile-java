package com.wdtinc.mapbox_vector_tile.adapt.jts;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import com.wdtinc.mapbox_vector_tile.adapt.jts.model.JtsLayer;
import com.wdtinc.mapbox_vector_tile.adapt.jts.model.JtsMvt;
import com.wdtinc.mapbox_vector_tile.util.JtsGeomStats;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test reading MVTs.
 */
public final class MvtReaderTest {

    @Test
    public void testLayers() {
        try {
            JtsMvt result = MvtReader.loadMvt(
                new File("src/test/resources/vec_tile_test/game.mvt"),
                new GeometryFactory(),
                new TagKeyValueMapConverter());

            final Collection<JtsLayer> layerValues = result.getLayers();
            final int actualCount = layerValues.size();
            final int expectedCount = 4;
            assertEquals(expectedCount, actualCount);

            assertTrue(result.getLayer("health") != null);
            assertTrue(result.getLayer("bombs") != null);
            assertTrue(result.getLayer("enemies") != null);
            assertTrue(result.getLayer("bullet") != null);

            // verify order
            final Iterator<JtsLayer> layerIterator = layerValues.iterator();
            assertTrue(layerIterator.next() == result.getLayer("bombs"));
            assertTrue(layerIterator.next() == result.getLayer("health"));
            assertTrue(layerIterator.next() == result.getLayer("enemies"));
            assertTrue(layerIterator.next() == result.getLayer("bullet"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void simpleTest() {
        try {
            // Load multipolygon z0 tile
            final JtsMvt mvt = loadMvt("src/test/resources/vec_tile_test/0/0/0.mvt");

            List<Geometry> geoms = getAllGeometries(mvt);

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
            final JtsMvt mvt = loadMvt(
                    "src/test/resources/mapbox/vector_tile_js/multi_poly_neg_exters.mvt",
                    MvtReader.RING_CLASSIFIER_V1);
            final List<Geometry> geoms = getAllGeometries(mvt);

            assertEquals(1, geoms.size());
            assertTrue(geoms.get(0) instanceof MultiPolygon);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private List<Geometry> getAllGeometries(JtsMvt mvt) {
        List<Geometry> allGeoms = new ArrayList<>();
        for (JtsLayer l : mvt.getLayers()) {
            allGeoms.addAll(l.getGeometries());
        }
        return allGeoms;
    }

    private static JtsMvt loadMvt(String file) throws IOException {
        return MvtReader.loadMvt(
                new File(file),
                new GeometryFactory(),
                new TagKeyValueMapConverter());
    }

    private static JtsMvt loadMvt(String file,
                                  MvtReader.RingClassifier ringClassifier) throws IOException {
        return MvtReader.loadMvt(
                new File(file),
                new GeometryFactory(),
                new TagKeyValueMapConverter(),
                ringClassifier);
    }
}
