package modularforcefields.client;

import modularforcefields.ModularForcefields;
import modularforcefields.client.render.tile.RenderFieldProjector;
import modularforcefields.client.screen.ScreenBiometricIdentifier;
import modularforcefields.client.screen.ScreenCoercionDeriver;
import modularforcefields.client.screen.ScreenFortronCapacitor;
import modularforcefields.client.screen.ScreenFortronFieldProjector;
import modularforcefields.client.screen.ScreenInterdictionMatrix;
import modularforcefields.registers.ModularForcefieldsMenuTypes;
import modularforcefields.registers.ModularForcefieldsTiles;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ModularForcefields.ID, bus = EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class MFFSClientRegister {
    public static final ResourceLocation MODEL_PREVIEWCUBE = ModularForcefields.rl("block/previewcube");
    public static final ResourceLocation MODEL_PREVIEWSPHERE = ModularForcefields.rl("block/previewsphere");
    public static final ResourceLocation MODEL_PREVIEWHALFSPHERE = ModularForcefields.rl("block/previewhalfsphere");
    public static final ResourceLocation MODEL_PREVIEWPYRAMID = ModularForcefields.rl("block/previewpyramid");
    public static final ResourceLocation MODEL_FIELDFORTRON = ModularForcefields.rl("block/fortronfieldprojector_fortron");

    @SubscribeEvent
    public static void onModelEvent(ModelEvent.RegisterAdditional event) {
        event.register(MODEL_PREVIEWCUBE);
        event.register(MODEL_PREVIEWSPHERE);
        event.register(MODEL_PREVIEWHALFSPHERE);
        event.register(MODEL_PREVIEWPYRAMID);
        event.register(MODEL_FIELDFORTRON);
    }

    @SubscribeEvent
    public static void registerEntities(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModularForcefieldsTiles.TILE_FORTRONFIELDPROJECTOR.get(), RenderFieldProjector::new);
    }

    public static void setup() {
    	MenuScreens.register(ModularForcefieldsMenuTypes.CONTAINER_COERCIONDERIVER.get(), ScreenCoercionDeriver::new);
        MenuScreens.register(ModularForcefieldsMenuTypes.CONTAINER_FORTRONCAPACITOR.get(), ScreenFortronCapacitor::new);
        MenuScreens.register(ModularForcefieldsMenuTypes.CONTAINER_FORTRONFIELDPROJECTOR.get(), ScreenFortronFieldProjector::new);
        MenuScreens.register(ModularForcefieldsMenuTypes.CONTAINER_INTERDICTIONMATRIX.get(), ScreenInterdictionMatrix::new);
        MenuScreens.register(ModularForcefieldsMenuTypes.CONTAINER_BIOMETRICIDENTIFIER.get(), ScreenBiometricIdentifier::new);
    }

}
