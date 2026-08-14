package modularforcefields.common.settings;

import voltaic.api.configuration.Configuration;
import voltaic.api.configuration.DoubleValue;
import voltaic.api.configuration.IntValue;

@Configuration(name = "Modular Forcefields")
public class MFFSConstants {

    @DoubleValue(def = 480.0)
    public static double COERCIONDERIVER_VOLTAGE = 480.0;

    @DoubleValue(def = 10_000_000.0)
    public static double FORTRONFIELD_MAXHEALTH = 10_000_000.0;

    @IntValue(def = 20 * 60)
    public static int BROKEN_FIELD_REBUILD_DELAY = 20 * 60;

    @IntValue(def = 512)
    public static int FIELD_CLEANUP_PER_TICK = 512;
}