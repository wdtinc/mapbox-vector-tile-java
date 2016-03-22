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


    /**
     * Return matching {@link Command} for the provided cmdId, or null if there is not
     * a matching command.
     *
     * @param cmdId command id to find match for
     * @return command with matching id, or null if there is not a matching command
     */
    public static Command fromId(int cmdId) {
        final Command command;
        switch (cmdId) {
            case 1:
                command = MoveTo;
                break;
            case 2:
                command = LineTo;
                break;
            case 7:
                command = ClosePath;
                break;
            default:
                command = null;
        }
        return command;
    }
}
