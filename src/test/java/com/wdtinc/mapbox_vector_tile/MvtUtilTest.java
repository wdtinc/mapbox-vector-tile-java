package com.wdtinc.mapbox_vector_tile;

import com.wdtinc.mapbox_vector_tile.encoding.GeomCmd;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public final class MvtUtilTest {

    @Test
    public void test() {
        assertEquals(GeomCmd.cmdHdr(Command.MoveTo, 1), 9);
        assertEquals(GeomCmd.cmdHdr(Command.MoveTo, 1) >> 3, 1);

        assertEquals(GeomCmd.getCmdId(GeomCmd.cmdHdr(Command.MoveTo, 1)), Command.MoveTo.getCmdId());
        assertEquals(GeomCmd.getCmdLength(GeomCmd.cmdHdr(Command.MoveTo, 1)), 1);

        Arrays.stream(Command.values()).forEach(c -> assertEquals(GeomCmd.cmdHdr(c, 1) & 0x7, c.getCmdId()));
    }
}
