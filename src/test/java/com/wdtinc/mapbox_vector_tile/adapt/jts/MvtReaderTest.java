package com.wdtinc.mapbox_vector_tile.adapt.jts;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;

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

    private static final double DOUBLE_DELTA = 1e-10;
    
    private static final int NUMBER_OF_DIMENSIONS = 2;
    private static final int SRID = 0;
    
    @Test
    public void testLayers() {
        try {
            JtsMvt result = MvtReader.loadMvt(
                new File("src/test/resources/vec_tile_test/game.mvt"),
                createGeometryFactory(),
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
            final MultiPolygon multiPolygon = (MultiPolygon) geoms.get(0);
            assertEquals(2, multiPolygon.getNumGeometries());
            {
                final Polygon polygon = (Polygon) multiPolygon.getGeometryN(0);
                assertEquals(0, polygon.getNumInteriorRing());
                final LineString exteriorRing = polygon.getExteriorRing();
                assertEquals(4, exteriorRing.getNumPoints());
                assertEquals(2059.0, exteriorRing.getCoordinateN(0).x, DOUBLE_DELTA);
                assertEquals(2048.0, exteriorRing.getCoordinateN(0).y, DOUBLE_DELTA);
                assertEquals(2048.0, exteriorRing.getCoordinateN(1).x, DOUBLE_DELTA);
                assertEquals(2048.0, exteriorRing.getCoordinateN(1).y, DOUBLE_DELTA);
                assertEquals(2059.0, exteriorRing.getCoordinateN(2).x, DOUBLE_DELTA);
                assertEquals(2037.0, exteriorRing.getCoordinateN(2).y, DOUBLE_DELTA);
                assertEquals(2059.0, exteriorRing.getCoordinateN(3).x, DOUBLE_DELTA);
                assertEquals(2048.0, exteriorRing.getCoordinateN(3).y, DOUBLE_DELTA);
            }
            {
                final Polygon polygon = (Polygon) multiPolygon.getGeometryN(1);
                assertEquals(0, polygon.getNumInteriorRing());
                final LineString exteriorRing = polygon.getExteriorRing();
                assertEquals(4, exteriorRing.getNumPoints());
                assertEquals(2037.0, exteriorRing.getCoordinateN(0).x, DOUBLE_DELTA);
                assertEquals(2059.0, exteriorRing.getCoordinateN(0).y, DOUBLE_DELTA);
                assertEquals(2037.0, exteriorRing.getCoordinateN(1).x, DOUBLE_DELTA);
                assertEquals(2048.0, exteriorRing.getCoordinateN(1).y, DOUBLE_DELTA);
                assertEquals(2048.0, exteriorRing.getCoordinateN(2).x, DOUBLE_DELTA);
                assertEquals(2048.0, exteriorRing.getCoordinateN(2).y, DOUBLE_DELTA);
                assertEquals(2037.0, exteriorRing.getCoordinateN(3).x, DOUBLE_DELTA);
                assertEquals(2059.0, exteriorRing.getCoordinateN(3).y, DOUBLE_DELTA);
            }
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
                createGeometryFactory(),
                new TagKeyValueMapConverter());
    }

    private static JtsMvt loadMvt(String file,
                                  MvtReader.RingClassifier ringClassifier) throws IOException {
        return MvtReader.loadMvt(
                new File(file),
                createGeometryFactory(),
                new TagKeyValueMapConverter(),
                ringClassifier);
    }
    
    private static GeometryFactory createGeometryFactory() {
        final PrecisionModel precisionModel = new PrecisionModel();
        final PackedCoordinateSequenceFactory coordinateSequenceFactory = 
                new PackedCoordinateSequenceFactory(PackedCoordinateSequenceFactory.DOUBLE, NUMBER_OF_DIMENSIONS);
        return new GeometryFactory(precisionModel, SRID, coordinateSequenceFactory);
    }
}
