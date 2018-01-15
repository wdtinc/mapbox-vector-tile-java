package com.wdtinc.mapbox_vector_tile.adapt.jts;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import com.wdtinc.mapbox_vector_tile.build.MvtLayerParams;
import com.wdtinc.mapbox_vector_tile.util.JdkUtils;

import java.util.List;

/**
 * Processing result containing intersection geometry and MVT geometry.
 *
 * @see JtsAdapter#createTileGeom(Geometry, Envelope, GeometryFactory, MvtLayerParams, IGeometryFilter)
 */
public final class TileGeomResult {

    /**
     * Intersection geometry (projection units and coordinates).
     */
    public final List<Geometry> intGeoms;

    /**
     * Geometry in MVT coordinates (tile extent units, screen coordinates).
     */
    public final List<Geometry> mvtGeoms;

    /**
     * Create TileGeomResult, which contains the intersection of geometry and MVT geometry.
     *
     * @param intGeoms geometry intersecting tile
     * @param mvtGeoms geometry for MVT
     * @throws NullPointerException if intGeoms or mvtGeoms are null
     */
    public TileGeomResult(List<Geometry> intGeoms, List<Geometry> mvtGeoms) {
        JdkUtils.requireNonNull(intGeoms);
        JdkUtils.requireNonNull(mvtGeoms);
        this.intGeoms = intGeoms;
        this.mvtGeoms = mvtGeoms;
    }
}
