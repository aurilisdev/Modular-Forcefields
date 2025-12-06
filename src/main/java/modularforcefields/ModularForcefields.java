package modularforcefields;

import modularforcefields.client.MFFSClientRegister;
import modularforcefields.common.block.BlockColorFortronField;
import modularforcefields.common.settings.MFFSConfig;
import modularforcefields.common.tags.MFFSTags;
import modularforcefields.registers.ModularForcefieldsBlocks;
import modularforcefields.registers.UnifiedModularForcefieldsRegister;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(ModularForcefields.ID)
@EventBusSubscriber(modid = ModularForcefields.ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModularForcefields {

    public static final String ID = "modularforcefields";
    public static final String NAME = "Modular Forcefields";

    public ModularForcefields(IEventBus bus, ModContainer container) {
	MFFSConfig.INSTANCE = new MFFSConfig();
	container.registerConfig(ModConfig.Type.COMMON, MFFSConfig.INSTANCE.SPEC);
	if (FMLEnvironment.dist == Dist.CLIENT) {
	    container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
	}
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

    }

    public static final ResourceLocation rl(String path) {
	return ResourceLocation.fromNamespaceAndPath(ModularForcefields.ID, path);
    }
}
