package modularforcefields.common.event;

import modularforcefields.ModularForcefields;
import modularforcefields.common.settings.MFFSConfig;
import modularforcefields.common.world.FortronFieldData;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = ModularForcefields.ID)
public final class FortronFieldEvents {

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
	if (!(event.getLevel() instanceof ServerLevel level)) {
	    return;
	}
	FortronFieldData.get(level).onChunkLoad(event.getChunk().getPos());
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
	if (!(event.getLevel() instanceof ServerLevel level)) {
	    return;
	}
	FortronFieldData.get(level).tickCleanup(level, MFFSConfig.INSTANCE.FIELD_CLEANUP_PER_TICK.get());
    }
}