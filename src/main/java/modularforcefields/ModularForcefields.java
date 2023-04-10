package modularforcefields;

import electrodynamics.prefab.configuration.ConfigurationHandler;
import modularforcefields.client.ClientRegister;
import modularforcefields.common.block.BlockColorFortronField;
import modularforcefields.common.packet.NetworkHandler;
import modularforcefields.common.settings.Constants;
import modularforcefields.common.tags.MFFTags;
import modularforcefields.registers.ModularForcefieldsBlocks;
import modularforcefields.registers.ModularForcefieldsItems;
import modularforcefields.registers.UnifiedModularForcefieldsRegister;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(References.ID)
@EventBusSubscriber(modid = References.ID, bus = Bus.MOD)
public class ModularForcefields {

	public ModularForcefields() {
		ConfigurationHandler.registerConfig(Constants.class);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		UnifiedModularForcefieldsRegister.register(bus);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ClientRegister.setup();
		});
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onColorEvent(RegisterColorHandlersEvent.Block event) {
		event.register(new BlockColorFortronField(), ModularForcefieldsBlocks.blockFortronField);
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		NetworkHandler.init();
		MFFTags.init();
		ModularForcefieldsItems.initItemMapping();
	}
}
