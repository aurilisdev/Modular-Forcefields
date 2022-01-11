package modularforcefields.common.settings;

import electrodynamics.api.configuration.Configuration;
import electrodynamics.api.configuration.DoubleValue;

@Configuration(name = "Modular Forcefields")
public class Constants {
	@DoubleValue(def = 480.0)
	public static double COERCIONDERIVER_VOLTAGE = 480.0;

}
