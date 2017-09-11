package com.wdtinc.mapbox_vector_tile.adapt.jts;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LayerTest {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    @Test
    public void testLayerName() {
        String layerName = "Points of Interest";
        Layer layer = new Layer(layerName);

        String actual = layer.getName();
        String expected = layerName;
        assertEquals(expected, actual);
    }

    @Test
    public void testLayerCollection() {
        String layerName = "Points of Interest";
        List<Geometry> geometries = new ArrayList<>();

        Layer layer = new Layer(layerName, geometries);

        String actualName = layer.getName();
        String expectedName = layerName;
        assertEquals(expectedName, actualName);

        Collection<Geometry> actualGeometry = layer.getGeometries();
        Collection<Geometry> expectedGeometry = geometries;
        assertEquals(expectedGeometry, actualGeometry);
    }

    @Test
    public void testAddGeometry() {
        String layerName = "Points of Interest";
        List<Geometry> geometries = new ArrayList<>();

        Point point = createPoint(new int[]{51, 0});

        Layer layer = new Layer(layerName, geometries);
        layer.add(point);

        assertTrue(layer.getGeometries().contains(point));
    }


    @Test
    public void testAddGeometries() {
        String layerName = "Points of Interest";
        List<Geometry> geometries = new ArrayList<>();

        Point point = createPoint(new int[]{50, 0});
        Point point2 = createPoint(new int[]{51, 1});
        Collection<Geometry> points = Arrays.asList(point, point2);

        Layer layer = new Layer(layerName, geometries);
        layer.addAll(points);

        assertTrue(layer.getGeometries().containsAll(Arrays.asList(point, point2)));
    }

    @Test
    public void testEquality() {
        Layer layer1 = new Layer("apples");
        Layer layer1Duplicate = new Layer("apples");
        assertTrue(layer1.equals(layer1Duplicate));

        Layer layer2 = new Layer("oranges");
        assertFalse(layer1.equals(layer2));
    }

    @Test
    public void testToString() {
        Layer layer1 = new Layer("apples");
        String actual = layer1.toString();
        String expected = "Layer{name='apples', geometries=[]}";
        assertEquals(expected, actual);
    }

    @Test
    public void testHash() {
        Layer layer = new Layer("code");
        int actual = layer.hashCode();
        int expected = 94834612;
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullName() {
        new Layer(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCollection() {
        new Layer("apples", null);
    }

    private Point createPoint(int[] coordinates) {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(coordinates[0], coordinates[1]));
    }
}
