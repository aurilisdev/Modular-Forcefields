package modularforcefields.client;

import modularforcefields.ModularForcefields;
import modularforcefields.client.guidebook.ModuleMFFS;
import modularforcefields.client.render.tile.RenderFieldProjector;
import modularforcefields.client.screen.ScreenBiometricIdentifier;
import modularforcefields.client.screen.ScreenCoercionDeriver;
import modularforcefields.client.screen.ScreenFortronCapacitor;
import modularforcefields.client.screen.ScreenFortronFieldProjector;
import modularforcefields.client.screen.ScreenInterdictionMatrix;
import modularforcefields.registers.ModularForcefieldsFluids;
import modularforcefields.registers.ModularForcefieldsMenuTypes;
import modularforcefields.registers.ModularForcefieldsTiles;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import voltaic.client.guidebook.ScreenGuidebook;
import voltaic.client.misc.SWBFClientExtensions;
import voltaic.common.fluid.SimpleWaterBasedFluidType;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ModularForcefields.ID, bus = EventBusSubscriber.Bus.MOD, value = { Dist.CLIENT })
public class MFFSClientRegister {
    public static final ModelResourceLocation MODEL_PREVIEWCUBE = ModelResourceLocation
	    .standalone(ModularForcefields.rl("block/previewcube"));
    public static final ModelResourceLocation MODEL_PREVIEWSPHERE = ModelResourceLocation
	    .standalone(ModularForcefields.rl("block/previewsphere"));
    public static final ModelResourceLocation MODEL_PREVIEWHALFSPHERE = ModelResourceLocation
	    .standalone(ModularForcefields.rl("block/previewhalfsphere"));
    public static final ModelResourceLocation MODEL_PREVIEWPYRAMID = ModelResourceLocation
	    .standalone(ModularForcefields.rl("block/previewpyramid"));
    public static final ModelResourceLocation MODEL_FIELDFORTRON = ModelResourceLocation
	    .standalone(ModularForcefields.rl("block/fortronfieldprojector_fortron"));

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
	event.registerBlockEntityRenderer(ModularForcefieldsTiles.TILE_FORTRONFIELDPROJECTOR.get(),
		RenderFieldProjector::new);
    }

    public static void setup() {
	ScreenGuidebook.addGuidebookModule(new ModuleMFFS());
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
	event.register(ModularForcefieldsMenuTypes.CONTAINER_COERCIONDERIVER.get(), ScreenCoercionDeriver::new);
	event.register(ModularForcefieldsMenuTypes.CONTAINER_FORTRONCAPACITOR.get(), ScreenFortronCapacitor::new);
	event.register(ModularForcefieldsMenuTypes.CONTAINER_FORTRONFIELDPROJECTOR.get(),
		ScreenFortronFieldProjector::new);
	event.register(ModularForcefieldsMenuTypes.CONTAINER_INTERDICTIONMATRIX.get(), ScreenInterdictionMatrix::new);
	event.register(ModularForcefieldsMenuTypes.CONTAINER_BIOMETRICIDENTIFIER.get(), ScreenBiometricIdentifier::new);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {

	ModularForcefieldsFluids.FLUIDS.getEntries().forEach(fluid -> {
	    event.registerFluidType(new SWBFClientExtensions((SimpleWaterBasedFluidType) fluid.get().getFluidType()),
		    fluid.get().getFluidType());
	});

    }

}
