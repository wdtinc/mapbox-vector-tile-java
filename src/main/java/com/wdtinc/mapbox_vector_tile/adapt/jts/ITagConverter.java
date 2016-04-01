package com.wdtinc.mapbox_vector_tile.adapt.jts;

import com.google.protobuf.ProtocolStringList;
import com.wdtinc.mapbox_vector_tile.VectorTile;

import java.util.List;

/**
 * Process MVT tags, convert to user data object.
 */
public interface ITagConverter {
    Object toUserData(List<Integer> tags, ProtocolStringList keysList, List<VectorTile.Tile.Value> valuesList);
}
