package modularforcefields.common.settings;

import voltaic.api.configuration.Configuration;
import voltaic.api.configuration.DoubleValue;

@Configuration(name = "Modular Forcefields")
public class MFFSConstants {
    @DoubleValue(def = 480.0)
    public static double COERCIONDERIVER_VOLTAGE = 480.0;

}
