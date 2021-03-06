package modularforcefields.client;

import modularforcefields.DeferredRegisters;
import modularforcefields.References;
import modularforcefields.client.screen.ScreenBiometricIdentifier;
import modularforcefields.client.screen.ScreenCoercionDeriver;
import modularforcefields.client.screen.ScreenFortronCapacitor;
import modularforcefields.client.screen.ScreenFortronFieldProjector;
import modularforcefields.client.screen.ScreenInterdictionMatrix;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = References.ID, bus = Bus.MOD, value = { Dist.CLIENT })
public class ClientRegister {
	@SubscribeEvent
	public static void onModelEvent(ModelRegistryEvent event) {
	}

	public static void setup() {
		MenuScreens.register(DeferredRegisters.CONTAINER_COERCIONDERIVER.get(), ScreenCoercionDeriver::new);
		MenuScreens.register(DeferredRegisters.CONTAINER_FORTRONCAPACITOR.get(), ScreenFortronCapacitor::new);
		MenuScreens.register(DeferredRegisters.CONTAINER_FORTRONFIELDPROJECTOR.get(), ScreenFortronFieldProjector::new);
		MenuScreens.register(DeferredRegisters.CONTAINER_INTERDICTIONMATRIX.get(), ScreenInterdictionMatrix::new);
		MenuScreens.register(DeferredRegisters.CONTAINER_BIOMETRICIDENTIFIER.get(), ScreenBiometricIdentifier::new);
	}

	@SubscribeEvent
	public static void registerEntities(EntityRenderersEvent.RegisterRenderers event) {
	}

	public static boolean shouldMultilayerRender(RenderType type) {
		return type == RenderType.translucent() || type == RenderType.solid();
	}

}
