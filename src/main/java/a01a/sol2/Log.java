package a01a.sol2;

import java.util.logging.Logger;

public class Log {
    private static Logger LOGGER;

    public static void setLOGGER(Logger LOGGERNEW) {
        LOGGER = LOGGERNEW;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }
}
