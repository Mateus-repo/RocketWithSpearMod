package com.strefiz.rocketwithspear.network;

import com.strefiz.rocketwithspear.config.ModConfig;
import com.strefiz.rocketwithspear.util.RocketUtil;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * Server-side registration of the optional custom packet. The server never
 * trusts the client: it re-checks gliding, spear charge, item presence and
 * cooldown before replicating the vanilla firework behaviour.
 */
public final class ModNetworking {

	private ModNetworking() {
	}

	public static void register() {
		// C2S payload type must be registered on both sides (client sends,
		// server receives).
		PayloadTypeRegistry.serverboundPlay().register(UseRocketPayload.TYPE, UseRocketPayload.CODEC);

		// The receiver only makes sense on a dedicated/integrated server.
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			ServerPlayNetworking.registerGlobalReceiver(UseRocketPayload.TYPE, (payload, context) -> {
				ServerPlayer player = context.player();
				InteractionHand hand = payload.hand();
				context.server().execute(() -> handleUseRocket(player, hand));
			});
		}
	}

	private static void handleUseRocket(ServerPlayer player, InteractionHand hand) {
		ModConfig config = ModConfig.get();
		if (!config.enabled) {
			return;
		}
		if (!player.isFallFlying()) {
			return;
		}
		if (config.requireSpearCharging && !player.isUsingItem()) {
			return;
		}

		ItemStack stack = player.getItemInHand(hand);
		if (!RocketUtil.isRocket(stack)) {
			return;
		}
		if (RocketUtil.isRocketOnCooldown(player)) {
			return;
		}

		// Replicates vanilla exactly: spawns the rocket, consumes it unless in
		// creative mode and awards the "used" statistic.
		player.gameMode.useItem(player, player.level(), stack, hand);
		RocketUtil.setRocketCooldown(player, config.cooldownTicks);
	}
}
