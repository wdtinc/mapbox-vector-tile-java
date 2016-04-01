package com.wdtinc.mapbox_vector_tile.adapt.jts;

import com.wdtinc.mapbox_vector_tile.VectorTile;

/**
 * Processes a user data object, converts to MVT feature tags.
 */
public interface IUserDataConverter {

    /**
     * Convert user data to MVT tags. The supplied user data may be null. The implementation
     * may add tags to the feature builder.
     *
     * @param userData user object may contain values in any format; may be null
     * @param layerProps properties global to the layer the feature belongs to
     * @param featureBuilder may be modified to contain additional tags
     */
    void addTags(Object userData, MvtLayerProps layerProps, VectorTile.Tile.Feature.Builder featureBuilder);
}
