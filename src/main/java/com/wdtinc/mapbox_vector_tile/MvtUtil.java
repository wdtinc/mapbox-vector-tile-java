package com.wdtinc.mapbox_vector_tile;

import com.wdtinc.mapbox_vector_tile.adapt.jts.MvtLayerProps;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Useful operations for encoding 'Mapbox Vector Tiles'.</p>
 *
 * <p>See: <a href="https://github.com/mapbox/vector-tile-spec">https://github.com/mapbox/vector-tile-spec</a></p>
 */
public final class MvtUtil {

    /**
     * Return whether the MVT geometry type should be closed with a {@link Command#ClosePath}.
     *
     * @param geomType the type of MVT geometry
     * @return true if the geometry should be closed, false if it should not be closed
     */
    public static boolean shouldClosePath(VectorTile.Tile.GeomType geomType) {
        final boolean closeReq;

        switch(geomType) {
            case POLYGON:
                closeReq = true;
                break;
            default:
                closeReq = false;
                break;
        }

        return closeReq;
    }

    /**
     * Create a new layer builder instance with initialized metadata.
     *
     * @param layerName name of the layer
     * @param mvtParams tile creation parameters
     * @return new layer builder instance with initialized metadata.
     */
    public static VectorTile.Tile.Layer.Builder newLayerBuilder(String layerName, MvtParams mvtParams) {
        final VectorTile.Tile.Layer.Builder layerBuilder = VectorTile.Tile.Layer.newBuilder();
        layerBuilder.setVersion(2);
        layerBuilder.setName(layerName);
        layerBuilder.setExtent(mvtParams.extent);

        return layerBuilder;
    }

    public static VectorTile.Tile.Value toValue(Object value) {
        final VectorTile.Tile.Value.Builder tileValue = VectorTile.Tile.Value.newBuilder();

        if(value instanceof Boolean) {
            tileValue.setBoolValue((Boolean) value);

        } else if(value instanceof Integer) {
            tileValue.setSintValue((Integer) value);

        } else if(value instanceof Long) {
            tileValue.setSintValue((Long) value);

        } else if(value instanceof Float) {
            tileValue.setFloatValue((Float) value);

        } else if(value instanceof Double) {
            tileValue.setDoubleValue((Double) value);

        } else if(value instanceof String) {
            tileValue.setStringValue((String) value);
        }

        return tileValue.build();
    }

    /**
     * Check if {@code value} is valid for encoding as a MVT layer property value.
     *
     * @param value target to check
     * @return true is the object is a type that is supported by MVT
     */
    public static boolean isValidPropValue(Object value) {
        boolean isValid = false;

        if(value instanceof Boolean || value instanceof Integer || value instanceof Long
                || value instanceof Float || value instanceof Double || value instanceof String) {
            isValid = true;
        }

        return isValid;
    }

    /**
     * Modifies {@code layerBuilder} to contain properties from {@code layerProps}.
     *
     * @param layerBuilder layer builder to write to
     * @param layerProps properties to write
     */
    public static void writeProps(VectorTile.Tile.Layer.Builder layerBuilder, MvtLayerProps layerProps) {

        // Add keys
        layerBuilder.addAllKeys(layerProps.getKeys());

        // Add values
        final Iterable<Object> vals = layerProps.getVals();
        vals.forEach(o -> layerBuilder.addValues(MvtUtil.toValue(o)));
    }

    /**
     * Convert an MVT value to String or boxed primitive object.
     *
     * @param value target for conversion
     * @return String or boxed primitive
     */
    public static Object valueToObj(VectorTile.Tile.Value value) {
        Object result = null;

        if(value.hasDoubleValue()) {
            result = value.getDoubleValue();

        } else if(value.hasFloatValue()) {
            result = value.getFloatValue();

        } else if(value.hasIntValue()) {
            result = value.getIntValue();

        } else if(value.hasBoolValue()) {
            result = value.getBoolValue();

        } else if(value.hasStringValue()) {
            result = value.getStringValue();

        } else if(value.hasSintValue()) {
            result = value.getSintValue();

        } else if(value.hasUintValue()) {
            result = value.getUintValue();
        }

        return result;
    }
}
