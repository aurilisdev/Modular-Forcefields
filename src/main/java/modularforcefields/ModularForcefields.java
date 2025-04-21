package modularforcefields;

import modularforcefields.client.ClientRegister;
import modularforcefields.common.block.BlockColorFortronField;
import modularforcefields.common.settings.MFFSConstants;
import modularforcefields.common.tags.MFFTags;
import modularforcefields.registers.ModularForcefieldsBlocks;
import modularforcefields.registers.UnifiedModularForcefieldsRegister;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import voltaic.prefab.configuration.ConfigurationHandler;

@Mod(ModularForcefields.ID)
@EventBusSubscriber(modid = ModularForcefields.ID, bus = EventBusSubscriber.Bus.MOD)
public class ModularForcefields {

    public static final String ID = "modularforcefields";
    public static final String NAME = "Modular Forcefields";

    public ModularForcefields(IEventBus bus) {
        ConfigurationHandler.registerConfig(MFFSConstants.class);
        UnifiedModularForcefieldsRegister.register(bus);
        MFFTags.init();
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
        event.register(new BlockColorFortronField(), ModularForcefieldsBlocks.BLOCK_FORTRONFIELD.get());
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {

    }

    public static final ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(ModularForcefields.ID, path);
    }
}
