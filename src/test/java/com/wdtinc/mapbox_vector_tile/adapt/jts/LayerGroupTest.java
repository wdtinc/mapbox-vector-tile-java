package com.wdtinc.mapbox_vector_tile.adapt.jts;

import java.util.Arrays;
import org.junit.Test;

import static org.junit.Assert.*;

public class LayerGroupTest {

    @Test
    public void testLayerAddition() {
        Layer layer1 = new Layer("first");
        Layer layer2 = new Layer("second");

        LayerGroup layers = new LayerGroup();
        layers.addLayer(layer1);
        layers.addLayer(layer2);

        assertTrue(layers.getLayers().containsAll(Arrays.asList(layer1, layer2)));
    }

    @Test
    public void testLayerByName() {
        Layer layer1 = new Layer("first");
        Layer layer2 = new Layer("second");

        LayerGroup layers = new LayerGroup();
        layers.addLayer(layer1);
        layers.addLayer(layer2);

        assertEquals(layer1, layers.getLayer("first"));
        assertEquals(layer2, layers.getLayer("second"));
    }

    @Test
    public void testLayerByIndex() {
        Layer layer1 = new Layer("first");
        Layer layer2 = new Layer("second");

        LayerGroup layers = new LayerGroup();
        layers.addLayer(layer1);
        layers.addLayer(layer2);

        assertEquals(layer1, layers.getLayer(0));
        assertEquals(layer2, layers.getLayer(1));
    }

    @Test
    public void testEquality() {
        Layer layer1 = new Layer("first");
        Layer layer2 = new Layer("second");

        LayerGroup layers = new LayerGroup();
        layers.addLayer(layer1);
        layers.addLayer(layer2);

        Layer duplicateLayer1 = new Layer("first");
        Layer duplicateLayer2 = new Layer("second");

        LayerGroup duplicateLayers = new LayerGroup();
        duplicateLayers.addLayer(duplicateLayer1);
        duplicateLayers.addLayer(duplicateLayer2);

        assertTrue(layers.equals(duplicateLayers));

        layers.addLayer(new Layer("extra"));
        assertFalse(layers.equals(duplicateLayers));
    }

    @Test
    public void testAddRemoveLayers() {
        Layer layer = new Layer("example");
        Layer layer2 = new Layer("example2");

        LayerGroup layers = new LayerGroup();
        layers.addLayers(layer, layer2);
        layers.getLayers().containsAll(Arrays.asList(layer, layer2));

        layers.removeLayer(layer);
        layers.getLayers().containsAll(Arrays.asList(layer2));
    }

    @Test
    public void testNoSuchLayer() {
        Layer layer = new Layer("example");

        LayerGroup layers = new LayerGroup();
        layers.addLayer(layer);

        Layer actual = layers.getLayer("No Such Layer");
        Layer expected = null;
        assertEquals(expected, actual);
    }

    @Test
    public void testToString() {
        Layer layer = new Layer("example");

        LayerGroup layers = new LayerGroup();
        layers.addLayer(layer);

        String actual = layers.toString();
        String expected = "LayerGroup{layers=[Layer{name='example', geometries=[]}]}";
        assertEquals(expected, actual);
    }

    @Test
    public void testHashcode() {
        Layer layer = new Layer("example");

        LayerGroup layers = new LayerGroup();
        layers.addLayer(layer);

        int actual = layers.hashCode();
        int expected = 1937578998;
        assertEquals(expected, actual);
    }
}
