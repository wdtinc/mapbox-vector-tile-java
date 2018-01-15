package com.wdtinc.mapbox_vector_tile.adapt.jts.model;

import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>JTS model of a Mapbox Vector Tile (MVT) layer.</p>
 *
 * <p>A layer contains a subset of all geographic geometries in the tile.</p>
 */
public class JtsLayer {

    private final String name;
    private final Collection<Geometry> geometries;

    /**
     * Create an empty JTS layer.
     *
     * @param name layer name
     * @throws IllegalArgumentException when {@code name} is null
     */
    public JtsLayer(String name) {
        this(name, new ArrayList<>(0));
    }

    /**
     * Create a JTS layer with geometries.
     *
     * @param name layer name
     * @param geometries
     * @throws IllegalArgumentException when {@code name} or {@code geometries} are null
     */
    public JtsLayer(String name, Collection<Geometry> geometries) {
        validate(name, geometries);
        this.name = name;
        this.geometries = geometries;
    }

    /**
     * Get a read-only collection of geometry.
     *
     * @return unmodifiable collection of geometry.
     */
    public Collection<Geometry> getGeometries() {
        return geometries;
    }

    /**
     * Get the layer name.
     *
     * @return name of the layer
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JtsLayer layer = (JtsLayer) o;

        if (name != null ? !name.equals(layer.name) : layer.name != null) return false;
        return geometries != null ? geometries.equals(layer.geometries) : layer.geometries == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (geometries != null ? geometries.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Layer{" +
                "name='" + name + '\'' +
                ", geometries=" + geometries +
                '}';
    }

    /**
     * Validate the JtsLayer.
     *
     * @param name mvt layer name
     * @param geometries geometries in the tile
     * @throws IllegalArgumentException when {@code name} or {@code geometries} are null
     */
    private static void validate(String name, Collection<Geometry> geometries) {
        if (name == null) {
            throw new IllegalArgumentException("layer name is null");
        }
        if (geometries == null) {
            throw new IllegalArgumentException("geometry collection is null");
        }
    }
}

