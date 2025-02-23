package modularforcefields.common.packet;

import modularforcefields.References;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = References.ID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
	private static final String PROTOCOL_VERSION = "1";

	@SubscribeEvent
	public static void registerPackets(final RegisterPayloadHandlersEvent event) {
		@SuppressWarnings("unused")
		final PayloadRegistrar registry = event.registrar(References.ID).versioned(PROTOCOL_VERSION).optional();

	}

	public static ResourceLocation id(String name) {
		return ResourceLocation.fromNamespaceAndPath(References.ID, name);
	}
}
