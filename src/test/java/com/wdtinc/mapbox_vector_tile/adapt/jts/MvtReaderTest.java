package com.wdtinc.mapbox_vector_tile.adapt.jts;

import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MvtReaderTest {

    @Test
    public void testLayers() {
        try {
            LayerGroup result = MvtReader.loadMvtWithLayers(
                    Paths.get("src/test/resources/vec_tile_test/game.mvt"),
                    new GeometryFactory(),
                    new TagKeyValueMapConverter());

            int actualCount = result.getLayers().size();
            int expectedCount = 4;
            assertEquals(expectedCount, actualCount);

            assertTrue(result.getLayer("health") != null);
            assertTrue(result.getLayer("bombs") != null);
            assertTrue(result.getLayer("enemies") != null);
            assertTrue(result.getLayer("bullet") != null);

            // verify order
            assertTrue(result.getLayer(0) == result.getLayer("bombs"));
            assertTrue(result.getLayer(1) == result.getLayer("health"));
            assertTrue(result.getLayer(2) == result.getLayer("enemies"));
            assertTrue(result.getLayer(3) == result.getLayer("bullet"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
