package modularforcefields.common.packet;

import modularforcefields.ModularForcefields;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ModularForcefields.ID)
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void registerPackets(final RegisterPayloadHandlersEvent event) {
	final PayloadRegistrar registry = event.registrar(ModularForcefields.ID).versioned(PROTOCOL_VERSION).optional();
	registry.playToClient(PacketFortronBeam.TYPE, PacketFortronBeam.CODEC, PacketFortronBeam::handle);

    }

}
