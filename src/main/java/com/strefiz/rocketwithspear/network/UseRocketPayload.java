package com.strefiz.rocketwithspear.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;

/**
 * C2S payload sent only when the server also runs this mod. It carries the hand
 * the client wants to use the rocket with; the server re-validates everything.
 */
public record UseRocketPayload(InteractionHand hand) implements CustomPacketPayload {

	public static final Type<UseRocketPayload> TYPE = CustomPacketPayload.createType("use_rocket");

	public static final StreamCodec<FriendlyByteBuf, UseRocketPayload> CODEC = CustomPacketPayload.codec(
			(payload, buf) -> buf.writeByte(payload.hand().ordinal()),
			buf -> new UseRocketPayload(InteractionHand.values()[buf.readByte()]));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
