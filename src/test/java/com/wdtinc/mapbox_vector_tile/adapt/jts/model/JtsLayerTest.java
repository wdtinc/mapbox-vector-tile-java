package com.wdtinc.mapbox_vector_tile.adapt.jts.model;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class JtsLayerTest {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    @Test
    public void testLayerName() {
        String layerName = "Points of Interest";
        JtsLayer layer = new JtsLayer(layerName);

        String actual = layer.getName();
        String expected = layerName;
        assertEquals(expected, actual);
    }

    @Test
    public void testLayerCollection() {
        String layerName = "Points of Interest";
        List<Geometry> geometries = new ArrayList<>();

        JtsLayer layer = new JtsLayer(layerName, geometries);

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

        JtsLayer layer = new JtsLayer(layerName, geometries);
        layer.getGeometries().add(point);

        assertTrue(layer.getGeometries().contains(point));
    }


    @Test
    public void testAddGeometries() {
        String layerName = "Points of Interest";
        List<Geometry> geometries = new ArrayList<>();

        Point point = createPoint(new int[]{50, 0});
        Point point2 = createPoint(new int[]{51, 1});
        Collection<Geometry> points = Arrays.asList(point, point2);

        JtsLayer layer = new JtsLayer(layerName, geometries);
        layer.getGeometries().addAll(points);

        assertTrue(layer.getGeometries().containsAll(Arrays.asList(point, point2)));
    }

    @Test
    public void testEquality() {
        JtsLayer layer1 = new JtsLayer("apples");
        JtsLayer layer1Duplicate = new JtsLayer("apples");
        assertTrue(layer1.equals(layer1Duplicate));

        JtsLayer layer2 = new JtsLayer("oranges");
        assertFalse(layer1.equals(layer2));
    }

    @Test
    public void testToString() {
        JtsLayer layer1 = new JtsLayer("apples");
        String actual = layer1.toString();
        String expected = "Layer{name='apples', geometries=[]}";
        assertEquals(expected, actual);
    }

    @Test
    public void testHash() {
        JtsLayer layer = new JtsLayer("code");
        int actual = layer.hashCode();
        int expected = 94834612;
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullName() {
        new JtsLayer(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCollection() {
        new JtsLayer("apples", null);
    }

    private Point createPoint(int[] coordinates) {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(coordinates[0], coordinates[1]));
    }
}
