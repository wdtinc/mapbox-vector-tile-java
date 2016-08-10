package com.wdtinc.mapbox_vector_tile;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
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
            final List<Geometry> geoms = MvtReader.loadMvt(
                    Paths.get("src/test/resources/vec_tile_test/0/0/0.mvt"),
                    new GeometryFactory(),
                    new TagKeyValueMapConverter());

            // Debug stats of multipolygon
            final JtsGeomStats stats = JtsGeomStats.getStats(geoms);
            LoggerFactory.getLogger(MvtReaderTest.class).info("Stats: {}", stats);

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
