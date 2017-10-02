package com.wdtinc.mapbox_vector_tile.adapt.jts.model;

import java.util.*;

/**
 * JTS model of a Mapbox Vector Tile.
 */
public class JtsMvt {

    /** Map layers by name */
    private final Map<String, JtsLayer> layersByName;

    /**
     * Create an empty MVT.
     */
    public JtsMvt() {
        this(Collections.emptyList());
    }

    /**
     * Create MVT with single layer.
     *
     * @param layer single MVT layer
     */
    public JtsMvt(JtsLayer layer) {
        this(Collections.singletonList(layer));
    }

    /**
     * Create MVT with the provided layers.
     *
     * @param layers multiple MVT layers
     */
    public JtsMvt(JtsLayer... layers) {
        this(new ArrayList<>(Arrays.asList(layers)));
    }

    /**
     * Create a MVT with the provided layers.
     *
     * @param layers multiple MVT layers
     */
    public JtsMvt(Collection<JtsLayer> layers) {

        // Linked hash map to preserve ordering
        layersByName = new LinkedHashMap<>(layers.size());

        for(JtsLayer nextLayer : layers) {
            layersByName.put(nextLayer.getName(), nextLayer);
        }
    }

    /**
     * @param name layer name
     * @return layer with matching name, or null if none exists
     */
    public JtsLayer getLayer(String name) {
        return layersByName.get(name);
    }

    /**
     * @return mapping of layer name to layer
     */
    public Map<String, JtsLayer> getLayersByName() {
        return layersByName;
    }

    /**
     * @return insertion-ordered collection of layers
     */
    public Collection<JtsLayer> getLayers() {
        return layersByName.values();
    }

    @Override
    public String toString() {
        return "JtsMvt{" +
                "layersByName=" + layersByName +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JtsMvt jtsMvt = (JtsMvt) o;

        return layersByName.equals(jtsMvt.layersByName);
    }

    @Override
    public int hashCode() {
        return layersByName.hashCode();
    }
}
