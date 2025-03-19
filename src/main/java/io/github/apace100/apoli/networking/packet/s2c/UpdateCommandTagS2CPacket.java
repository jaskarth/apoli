package io.github.apace100.apoli.networking.packet.s2c;

import io.github.apace100.apoli.Apoli;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record UpdateCommandTagS2CPacket(int entityId, String tag, boolean added) implements CustomPayload {

	public static final Id<UpdateCommandTagS2CPacket> PACKET_ID = new Id<>(Apoli.identifier("s2c/update_command_tag"));
	public static final PacketCodec<ByteBuf, UpdateCommandTagS2CPacket> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.VAR_INT, UpdateCommandTagS2CPacket::entityId,
			PacketCodecs.STRING, UpdateCommandTagS2CPacket::tag,
			PacketCodecs.BOOL, UpdateCommandTagS2CPacket::added,
			UpdateCommandTagS2CPacket::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return PACKET_ID;
	}
}
