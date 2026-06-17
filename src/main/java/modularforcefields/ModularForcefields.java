package modularforcefields;

import modularforcefields.client.MFFSClientRegister;
import modularforcefields.common.block.BlockColorFortronField;
import modularforcefields.common.packet.NetworkHandler;
import modularforcefields.common.settings.MFFSConstants;
import modularforcefields.common.tags.MFFSTags;
import modularforcefields.registers.ModularForcefieldsBlocks;
import modularforcefields.registers.UnifiedModularForcefieldsRegister;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import voltaic.prefab.configuration.ConfigurationHandler;

@Mod(ModularForcefields.ID)
@EventBusSubscriber(modid = ModularForcefields.ID, bus = EventBusSubscriber.Bus.MOD)
public class ModularForcefields {

    public static final String ID = "modularforcefields";
    public static final String NAME = "Modular Forcefields";

    public ModularForcefields() {
	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
	ConfigurationHandler.registerConfig(MFFSConstants.class);
	UnifiedModularForcefieldsRegister.register(bus);
	MFFSTags.init();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onClientSetup(FMLClientSetupEvent event) {
	event.enqueueWork(() -> {
	    MFFSClientRegister.setup();
	});
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onColorEvent(RegisterColorHandlersEvent.Block event) {
	event.register(new BlockColorFortronField(), ModularForcefieldsBlocks.BLOCK_FORTRONFIELD.get());
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
	NetworkHandler.init();
    }

    public static final ResourceLocation rl(String path) {
	return new ResourceLocation(ModularForcefields.ID, path);
    }
}
