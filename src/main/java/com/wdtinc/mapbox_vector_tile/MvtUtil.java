package com.wdtinc.mapbox_vector_tile;

import com.wdtinc.mapbox_vector_tile.util.Vec2d;

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
     * Convert tile 'pixel' position to MVT position. Modifies mvtPos to contain position value.
     *
     * @param tilePos tile pixel position in world coordinate system (y=0 at bottom)
     * @param mvtParams defines tile world pixel dims and MVT extent dims
     * @param mvtPos modified to contain the MVT position (y=0 at top, scaled by ratio)
     */
    public static void toMvtCoord(Vec2d tilePos, MvtParams mvtParams, Vec2d mvtPos) {

        // Set mvtPos to y-inverted tilePos position w.r.t. tileHeight, scaled by ratio
        mvtPos.set(tilePos.x, mvtParams.tileSize).sub(0d, tilePos.y).scale(mvtParams.ratio);
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

    public static VectorTile.Tile.Value.Builder setType(Object value) {
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

        return tileValue;
    }
}
