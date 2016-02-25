package com.wdtinc.mapbox_vector_tile;

/**
 * MVT Draw Command.
 */
public enum Command {
    MoveTo(1, 2),
    LineTo(2, 2),
    ClosePath(7, 0);

    /** Unique command ID */
    private final int cmdId;

    /** Amount of parameters that follow the command */
    private final int paramCount;

    Command(int cmdId, int paramCount) {
        this.cmdId = cmdId;
        this.paramCount = paramCount;
    }

    /**
     * @return unique command ID
     */
    public int getCmdId() {
        return cmdId;
    }

    /**
     * @return amount of parameters that follow the command
     */
    public int getParamCount() {
        return paramCount;
    }
}
