package com.wdtinc.mapbox_vector_tile.adapt.jts;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;

/**
 * Round each coordinate value to an integer.
 */
public final class RoundingFilter implements CoordinateSequenceFilter {

    public static final RoundingFilter INSTANCE = new RoundingFilter();

    private RoundingFilter() {}

    @Override
    public void filter(CoordinateSequence seq, int i) {
        seq.setOrdinate(i, 0, Math.round(seq.getOrdinate(i, 0)));
        seq.setOrdinate(i, 1, Math.round(seq.getOrdinate(i, 1)));
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean isGeometryChanged() {
        return true;
    }
}
