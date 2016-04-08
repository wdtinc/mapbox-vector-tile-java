package com.wdtinc.mapbox_vector_tile.adapt.jts;

import com.wdtinc.mapbox_vector_tile.VectorTile;
import com.wdtinc.mapbox_vector_tile.build.MvtLayerProps;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Convert simple user data {@link Map} where the keys are {@link String} and values are {@link Object}.
 *
 * @see IUserDataConverter
 */
public final class UserDataKeyValueMapConverter implements IUserDataConverter {
    @Override
    public void addTags(Object userData, MvtLayerProps layerProps, VectorTile.Tile.Feature.Builder featureBuilder) {
        if(userData != null) {
            try {
                @SuppressWarnings("unchecked")
                final Map<String, Object> userDataMap = (Map<String, Object>)userData;

                userDataMap.entrySet().stream().forEach(e -> {
                    final String key = e.getKey();
                    final Object value = e.getValue();

                    if(key != null && value != null) {
                        final int valueIndex = layerProps.addValue(value);

                        if(valueIndex >= 0) {
                            featureBuilder.addTags(layerProps.addKey(key));
                            featureBuilder.addTags(valueIndex);
                        }
                    }
                });

            } catch (ClassCastException e) {
                LoggerFactory.getLogger(UserDataKeyValueMapConverter.class).error(e.getMessage(), e);
            }
        }
    }
}
