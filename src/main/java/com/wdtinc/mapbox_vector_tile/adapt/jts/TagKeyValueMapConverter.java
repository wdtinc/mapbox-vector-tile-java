package com.wdtinc.mapbox_vector_tile.adapt.jts;

import com.google.protobuf.ProtocolStringList;
import com.wdtinc.mapbox_vector_tile.MvtUtil;
import com.wdtinc.mapbox_vector_tile.VectorTile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TagKeyValueMapConverter implements ITagConverter {

    /** If true, return null user data when tags are empty */
    private final boolean nullIfEmpty;

    /**
     * Always created user data object, even with empty tags.
     */
    public TagKeyValueMapConverter() {
        this(false);
    }

    /**
     * @param nullIfEmpty if true, return null user data when tags are empty
     */
    public TagKeyValueMapConverter(boolean nullIfEmpty) {
        this.nullIfEmpty = nullIfEmpty;
    }

    @Override
    public Object toUserData(List<Integer> tags, ProtocolStringList keysList, List<VectorTile.Tile.Value> valuesList) {

        // Guard: empty
        if(nullIfEmpty && tags.size() < 1) {
            return null;
        }


        final Map<String, Object> userData = new HashMap<>(((tags.size() + 1) / 2));

        int keyIndex;
        int valIndex;
        boolean valid;

        for(int i = 0; i < tags.size() - 1; i += 2) {
            keyIndex = tags.get(i);
            valIndex = tags.get(i + 1);

            valid = keyIndex >= 0 && keyIndex < keysList.size()
                    && valIndex >= 0 && valIndex < valuesList.size();

            if(valid) {
                userData.put(keysList.get(keyIndex), MvtUtil.valueToObj(valuesList.get(valIndex)));
            }
        }

        return userData;
    }
}
