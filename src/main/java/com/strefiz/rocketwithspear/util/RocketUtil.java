package com.strefiz.rocketwithspear.util;

import com.strefiz.rocketwithspear.config.ModConfig;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Shared, side-agnostic helpers for the rocket keybind. */
public final class RocketUtil {

	private static final ItemStack ROCKET = new ItemStack(Items.FIREWORK_ROCKET);

	private RocketUtil() {
	}

	/** True when the stack is a firework rocket. */
	public static boolean isRocket(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() == Items.FIREWORK_ROCKET;
	}

	/**
	 * True when the player is currently charging a spear or trident.
	 *
	 * <p>Minecraft 26.x introduced the spear weapons (tagged {@code SPEARS}) in
	 * addition to the trident, so both are accepted.
	 */
	public static boolean isChargingSpear(Player player) {
		if (!player.isUsingItem()) {
			return false;
		}
		ItemStack used = player.getItemInHand(player.getUsedItemHand());
		return used.typeHolder().is(ItemTags.SPEARS) || used.getItem() == Items.TRIDENT;
	}

	/**
	 * Finds the hand that holds a rocket and is not the hand currently busy
	 * charging the spear.
	 *
	 * @return the hand to use, or {@code null} if no valid rocket is held
	 */
	public static InteractionHand findRocketHand(Player player, ModConfig config) {
		InteractionHand busyHand = player.getUsedItemHand();
		boolean offHandPreferred = config.useOffHandPreferred();

		if (offHandPreferred) {
			if (isRocket(player.getOffhandItem()) && busyHand != InteractionHand.OFF_HAND) {
				return InteractionHand.OFF_HAND;
			}
			if (isRocket(player.getMainHandItem()) && busyHand != InteractionHand.MAIN_HAND) {
				return InteractionHand.MAIN_HAND;
			}
		} else {
			if (isRocket(player.getMainHandItem()) && busyHand != InteractionHand.MAIN_HAND) {
				return InteractionHand.MAIN_HAND;
			}
			if (isRocket(player.getOffhandItem()) && busyHand != InteractionHand.OFF_HAND) {
				return InteractionHand.OFF_HAND;
			}
		}
		return null;
	}

	/** True when the firework rocket is on the player's local cooldown. */
	public static boolean isRocketOnCooldown(Player player) {
		return player.getCooldowns().isOnCooldown(ROCKET);
	}

	/** Adds a cooldown to the firework rocket (ticks, 0 disables it). */
	public static void setRocketCooldown(Player player, int ticks) {
		if (ticks > 0) {
			player.getCooldowns().addCooldown(ROCKET, ticks);
		}
	}
}
