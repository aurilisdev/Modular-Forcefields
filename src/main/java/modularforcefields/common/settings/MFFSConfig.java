package modularforcefields.common.settings;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MFFSConfig {
    public static MFFSConfig INSTANCE;

    public ModConfigSpec SPEC;
    public ModConfigSpec.DoubleValue COERCIONDERIVER_VOLTAGE;
    public ModConfigSpec.DoubleValue FORTRONFIELD_MAXHEALTH;
    public ModConfigSpec.IntValue BROKEN_FIELD_REBUILD_DELAY;
    public ModConfigSpec.IntValue FIELD_CLEANUP_PER_TICK;

    public MFFSConfig() {
	ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
	builder.push("common");
	COERCIONDERIVER_VOLTAGE = builder.defineInRange("coercionderiver_voltage", 480.0, 0, Double.MAX_VALUE);
	FORTRONFIELD_MAXHEALTH = builder.defineInRange("fortronfield_maxhealth", 10_000_000, 1, Double.MAX_VALUE);
	BROKEN_FIELD_REBUILD_DELAY = builder.defineInRange("broken_field_rebuild_delay", 20 * 60, 1, Integer.MAX_VALUE);
	BROKEN_FIELD_REBUILD_DELAY = builder.defineInRange("field_cleanup_per_tick", 512, 1, Integer.MAX_VALUE);
	builder.pop();
	SPEC = builder.build();
    }
}
