package com.wdtinc.mapbox_vector_tile;

import com.vividsolutions.jts.geom.*;
import com.wdtinc.mapbox_vector_tile.adapt.jts.MvtReader;
import com.wdtinc.mapbox_vector_tile.adapt.jts.TagKeyValueMapConverter;
import com.wdtinc.mapbox_vector_tile.util.JtsGeomStats;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
            final List<Geometry> geoms = loadGeoms(
                    "src/test/resources/mapbox/vector_tile_js/multi_poly_neg_exters.mvt");

            assertEquals(1, geoms.size());
            assertTrue(geoms.get(0) instanceof MultiPolygon);

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTippPolys() {
        try {
            final Logger logger = LoggerFactory.getLogger(MvtReaderTest.class);

            // Tippecanoe isles data set
            final List<Geometry> geoms = loadGeoms("src/test/resources/mapbox/tipp/historical_output.mvt");

            geoms.forEach(nextGeom -> {
                if(!nextGeom.isValid()) {
                    logger.info("NOT VALID! {}", nextGeom);
                }

                if(!nextGeom.isSimple()) {
                    logger.info("NOT SIMPLE! {}", nextGeom);
                }
            });

            // Debug stats of multipolygon
            final JtsGeomStats stats = JtsGeomStats.getStats(geoms);
            LoggerFactory.getLogger(MvtReaderTest.class).info("Stats: {}", stats);

            // Count rings
            final AtomicInteger extRings = new AtomicInteger();
            final AtomicInteger holes = new AtomicInteger();
            geoms.stream()
                    .filter(g -> g instanceof Polygon)
                    .map(g -> (Polygon)g)
                    .forEach(p -> {
                        extRings.getAndIncrement();
                        holes.getAndAdd(p.getNumInteriorRing());
                    });
            geoms.stream()
                    .filter(g -> g instanceof MultiPolygon)
                    .map(g -> (MultiPolygon)g)
                    .forEach(mp -> {
                        for(int i = 0; i < mp.getNumGeometries(); ++i) {
                            final Polygon p = (Polygon)mp.getGeometryN(i);
                            extRings.getAndIncrement();
                            holes.getAndAdd(p.getNumInteriorRing());
                        }
                    });

            logger.info("Exterior rings: {}, Holes: {}", extRings, holes);

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
}
