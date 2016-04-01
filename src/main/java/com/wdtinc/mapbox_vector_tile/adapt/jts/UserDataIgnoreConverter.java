package com.wdtinc.mapbox_vector_tile.adapt.jts;

import com.wdtinc.mapbox_vector_tile.VectorTile;

/**
 * Ignores user data, does not take any action.
 */
public final class UserDataIgnoreConverter implements IUserDataConverter {
    @Override
    public void addTags(Object userData, MvtLayerProps layerProps, VectorTile.Tile.Feature.Builder featureBuilder) {}
}
