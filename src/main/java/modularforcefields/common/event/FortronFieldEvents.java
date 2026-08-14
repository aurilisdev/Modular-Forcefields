package modularforcefields.common.event;

import modularforcefields.ModularForcefields;
import modularforcefields.common.settings.MFFSConstants;
import modularforcefields.common.world.FortronFieldData;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModularForcefields.ID)
public final class FortronFieldEvents {

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
	if (!(event.getLevel() instanceof ServerLevel level)) {
	    return;
	}

	FortronFieldData.get(level).onChunkLoad(event.getChunk().getPos());
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
	if (event.phase != TickEvent.Phase.END) {
	    return;
	}

	if (!(event.level instanceof ServerLevel level)) {
	    return;
	}

	FortronFieldData.get(level).tickCleanup(level, MFFSConstants.FIELD_CLEANUP_PER_TICK);
    }
}