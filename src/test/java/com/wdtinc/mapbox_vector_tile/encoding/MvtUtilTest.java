package com.wdtinc.mapbox_vector_tile.encoding;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Test MVT utility functions.
 */
public final class MvtUtilTest {

    @Test
    public void testHeaders() {
        assertEquals(GeomCmdHdr.cmdHdr(GeomCmd.MoveTo, 1), 9);
        assertEquals(GeomCmdHdr.cmdHdr(GeomCmd.MoveTo, 1) >> 3, 1);

        assertEquals(GeomCmdHdr.getCmdId(GeomCmdHdr.cmdHdr(GeomCmd.MoveTo, 1)), GeomCmd.MoveTo.getCmdId());
        assertEquals(GeomCmdHdr.getCmdLength(GeomCmdHdr.cmdHdr(GeomCmd.MoveTo, 1)), 1);

        Arrays.stream(GeomCmd.values()).forEach(c -> assertEquals(GeomCmdHdr.cmdHdr(c, 1) & 0x7, c.getCmdId()));
    }
}
