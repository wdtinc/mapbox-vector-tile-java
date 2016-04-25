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

public final class MvtReaderTest {

    @Test
    public void simpleTest() {
//        try {
//            GeometryFactory geometryFactory = new GeometryFactory();
//            final List<Geometry> geoms = MvtReader.loadMvt(Paths.get("src/test/resources/vec_tile_test/0/0/L5_X7_Y13_dp.mvt"),
//                    geometryFactory, new TagKeyValueMapConverter());
//            final List<Geometry> geoms2 = MvtReader.loadMvt(Paths.get("src/test/resources/vec_tile_test/0/0/L5_X7_Y13_tps.mvt"),
//                    geometryFactory, new TagKeyValueMapConverter());

//            geoms.forEach(g -> {
//                System.out.println(g);
//                System.out.println(g.getUserData());
//            });

//            final JtsGeomStats stats = JtsGeomStats.getStats(geoms);
//            final JtsGeomStats stats2 = JtsGeomStats.getStats(geoms2);
//            LoggerFactory.getLogger(MvtReaderTest.class).info("Stats DP: {}", stats);
//            LoggerFactory.getLogger(MvtReaderTest.class).info("Stats TPS: {}", stats2);
//
//        } catch (IOException e) {
//            fail(e.getMessage());
//        }
    }
}
