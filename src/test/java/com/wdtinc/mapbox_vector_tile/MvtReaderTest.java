package com.wdtinc.mapbox_vector_tile;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.wdtinc.mapbox_vector_tile.adapt.jts.MvtReader;
import com.wdtinc.mapbox_vector_tile.adapt.jts.TagKeyValueMapConverter;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public final class MvtReaderTest {

    @Test
    public void simpleTest() {
        try {
            GeometryFactory geometryFactory = new GeometryFactory();
            final List<Geometry> geoms = MvtReader.loadMvt(Paths.get("src/test/resources/vec_tile_test/0/0/0.mvt"),
                    geometryFactory, new TagKeyValueMapConverter());

//            geoms.forEach(g -> {
//                System.out.println(g);
//                System.out.println(g.getUserData());
//            });

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
