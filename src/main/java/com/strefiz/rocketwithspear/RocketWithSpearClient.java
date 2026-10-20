package com.strefiz.rocketwithspear;

import com.mojang.blaze3d.platform.InputConstants;
import com.strefiz.rocketwithspear.config.ModConfig;
import com.strefiz.rocketwithspear.network.UseRocketPayload;
import com.strefiz.rocketwithspear.util.RocketUtil;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.SwingAnimation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Client entrypoint: registers the keybind and performs the rocket use.
 *
 * <p>Vanilla blocks new "use" actions while the player is using another item
 * ({@code Minecraft#startUseItem} returns early when {@code isHandsBusy()}).
 * Calling {@code MultiPlayerGameMode#useItem} directly bypasses that client-side
 * check, and the vanilla server accepts the resulting
 * {@code ServerboundUseItemPacket} because {@code ServerPlayerGameMode#useItem}
 * does not verify that the player is not already using another item.
 *
 * <p>If the server also runs this mod (detected with
 * {@link ClientPlayNetworking#canSend}), a custom payload is sent instead so the
 * server can validate and replicate the behaviour (Phase 2).
 */
public class RocketWithSpearClient implements ClientModInitializer {

	private static final Logger LOGGER = LogManager.getLogger("RocketWithSpear");

	/**
	 * Default key, resolved from the vanilla "V" key name (re-bindable in the
	 * controls menu). Falls back to {@code GLFW_KEY_V} (86) if not found.
	 */
	private static final int DEFAULT_KEY = resolveDefaultKey();

	private static int resolveDefaultKey() {
		try {
			return com.mojang.blaze3d.platform.InputConstants.getKey("key.keyboard.v").getValue();
		} catch (RuntimeException e) {
			return 25; // SDL_SCANCODE_V
		}
	}

	public static final KeyMapping USE_ROCKET_KEY = new KeyMapping(
			"key.rocketwithspear.use_rocket",
			InputConstants.Type.KEYBOARD,
			DEFAULT_KEY,
			KeyMapping.Category.GAMEPLAY);

	@Override
	public void onInitializeClient() {
		// Preferred path. If it is too late (Options already initialised) the
		// OptionsMixin adds the keybind instead.
		try {
			KeyMappingHelper.registerKeyMapping(USE_ROCKET_KEY);
		} catch (Throwable t) {
			LOGGER.debug("Could not register keybind through Fabric helper; the mixin will add it.", t);
		}
		ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
	}

	private void onClientTick(Minecraft minecraft) {
		ModConfig config = ModConfig.get();
		if (!config.enabled) {
			return;
		}
		while (USE_ROCKET_KEY.consumeClick()) {
			tryUseRocket(minecraft, config);
		}
	}

	private void tryUseRocket(Minecraft minecraft, ModConfig config) {
		if (minecraft.player == null || minecraft.level == null || minecraft.gameMode == null) {
			return;
		}
		var player = minecraft.player;
		if (player.isSpectator()) {
			return;
		}
		if (config.requireFlying && !player.isFallFlying()) {
			return;
		}
		if (config.requireSpearCharging && !RocketUtil.isChargingSpear(player)) {
			return;
		}

		InteractionHand hand = RocketUtil.findRocketHand(player, config);
		if (hand == null) {
			return;
		}
		if (RocketUtil.isRocketOnCooldown(player)) {
			return;
		}

		// Phase 2: let the server validate and replicate when it also has the mod.
		if (config.allowServerMod && ClientPlayNetworking.canSend(UseRocketPayload.TYPE)) {
			ClientPlayNetworking.send(new UseRocketPayload(hand));
			return;
		}

		// Phase 1: vanilla-compatible, client-only.
		player.swing(hand, SwingAnimation.DEFAULT, false);
		minecraft.gameMode.useItem(player, hand);
		RocketUtil.setRocketCooldown(player, config.cooldownTicks);
	}
}
