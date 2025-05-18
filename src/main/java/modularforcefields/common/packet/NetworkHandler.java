package modularforcefields.common.packet;

import modularforcefields.ModularForcefields;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
	private static final String PROTOCOL_VERSION = "1";
	// private static int disc = 0;
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(ModularForcefields.rl("main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static void init() {
	}
}
