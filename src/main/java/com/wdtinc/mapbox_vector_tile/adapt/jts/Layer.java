package com.wdtinc.mapbox_vector_tile.adapt.jts;

import com.vividsolutions.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A layer contains a subset of all geographic geometries.
 */
public class Layer {

    private final String name;
    private final Collection<Geometry> geometries;

    public Layer(String name) {
        this(name, new ArrayList<>());
    }

    public Layer(String name, Collection<Geometry> geometries) {
        check(name, geometries);
        this.name = name;
        this.geometries = geometries;
    }

    public void add(Geometry geometry) {
        geometries.add(geometry);
    }

    public void addAll(Collection<Geometry> geometries) {
        this.geometries.addAll(geometries);
    }

    public Collection<Geometry> getGeometries() {
        return new ArrayList<>(geometries);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Layer layer = (Layer) o;

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

    private static void check(String name, Collection<Geometry> geometries) {
        if (name == null) {
            throw new IllegalArgumentException("layer name is null");
        }
        if (geometries == null) {
            throw new IllegalArgumentException("geometry collection is null");
        }
    }
}

