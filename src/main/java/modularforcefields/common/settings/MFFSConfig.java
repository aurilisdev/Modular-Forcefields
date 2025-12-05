package modularforcefields.common.settings;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MFFSConfig {
    public static MFFSConfig INSTANCE;

    public ModConfigSpec SPEC;
    public ModConfigSpec.DoubleValue COERCIONDERIVER_VOLTAGE;

    public MFFSConfig() {
	ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
	builder.push("common");
	COERCIONDERIVER_VOLTAGE = builder.defineInRange("coercionderiver_voltage", 480.0, 0, Double.MAX_VALUE);
	builder.pop();
	SPEC = builder.build();
    }
}
