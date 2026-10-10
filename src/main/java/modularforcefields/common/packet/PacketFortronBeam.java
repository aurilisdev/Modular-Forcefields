package modularforcefields.common.packet;

import modularforcefields.ModularForcefields;
import modularforcefields.client.render.FortronBeamRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Tells nearby clients to render a short-lived fortron ray between two points.
 */
public record PacketFortronBeam(double fromX, double fromY, double fromZ, double toX, double toY, double toZ, int color,
	int duration) implements CustomPacketPayload {

    public static final Type<PacketFortronBeam> TYPE = new Type<>(ModularForcefields.rl("fortronbeam"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketFortronBeam> CODEC = StreamCodec
	    .of(PacketFortronBeam::write, PacketFortronBeam::read);
    public static final int DEFAULT_COLOR = 0x4FA8FF;
    public static final int BREAK_COLOR = 0xFF3030;
    private static final double RANGE = 64.0;

    private static void write(FriendlyByteBuf buf, PacketFortronBeam p) {
	buf.writeDouble(p.fromX);
	buf.writeDouble(p.fromY);
	buf.writeDouble(p.fromZ);
	buf.writeDouble(p.toX);
	buf.writeDouble(p.toY);
	buf.writeDouble(p.toZ);
	buf.writeInt(p.color);
	buf.writeVarInt(p.duration);
    }

    private static PacketFortronBeam read(FriendlyByteBuf buf) {
	return new PacketFortronBeam(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(),
		buf.readDouble(), buf.readDouble(), buf.readInt(), buf.readVarInt());
    }

    public static void send(ServerLevel level, Vec3 from, Vec3 to, int color, int duration) {
	PacketDistributor.sendToPlayersNear(level, null, from.x, from.y, from.z, RANGE,
		new PacketFortronBeam(from.x, from.y, from.z, to.x, to.y, to.z, color, duration));
    }

    public static void handle(PacketFortronBeam packet, IPayloadContext context) {
	FortronBeamRenderer.addBeam(new Vec3(packet.fromX, packet.fromY, packet.fromZ),
		new Vec3(packet.toX, packet.toY, packet.toZ), packet.color, packet.duration);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
	return TYPE;
    }
}
