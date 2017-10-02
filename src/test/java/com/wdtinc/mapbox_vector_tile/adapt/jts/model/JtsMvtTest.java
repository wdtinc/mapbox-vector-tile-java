package com.wdtinc.mapbox_vector_tile.adapt.jts.model;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public final class JtsMvtTest {

    @Test
    public void testConstructor() {
        final JtsLayer layer1 = new JtsLayer("first");
        final JtsLayer layer2 = new JtsLayer("second");

        final JtsMvt mvt = new JtsMvt(layer1, layer2);
        assertTrue(mvt.getLayers().containsAll(asList(layer1, layer2)));
    }

    @Test
    public void testLayerByName() {
        final JtsLayer layer1 = new JtsLayer("first");
        final JtsLayer layer2 = new JtsLayer("second");

        final JtsMvt mvt = new JtsMvt(layer1, layer2);

        assertEquals(layer1, mvt.getLayer("first"));
        assertEquals(layer2, mvt.getLayer("second"));
    }

    @Test
    public void testEquality() {
        final JtsLayer layer1 = new JtsLayer("first");
        final JtsLayer layer2 = new JtsLayer("second");

        final JtsMvt mvt = new JtsMvt(layer1, layer2);

        final JtsLayer duplicateLayer1 = new JtsLayer("first");
        final JtsLayer duplicateLayer2 = new JtsLayer("second");

        final JtsMvt mvt2 = new JtsMvt(duplicateLayer1, duplicateLayer2);

        assertTrue(mvt.equals(mvt2));

        final JtsMvt mvt3 = new JtsMvt(duplicateLayer1, duplicateLayer2, new JtsLayer("extra"));

        assertFalse(mvt.equals(mvt3));
    }

    @Test
    public void testNoSuchLayer() {
        final JtsLayer layer = new JtsLayer("example");
        final JtsMvt mvt = new JtsMvt(layer);

        assertNull(mvt.getLayer("No Such Layer"));
    }
}
