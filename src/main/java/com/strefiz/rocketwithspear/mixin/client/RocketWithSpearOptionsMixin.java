package com.strefiz.rocketwithspear.mixin.client;

import java.util.Arrays;

import com.strefiz.rocketwithspear.RocketWithSpearClient;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds the mod keybind to the vanilla controls list so it can be re-bound from
 * Options → Controls.
 *
 * <p>The Fabric {@code KeyMappingHelper.registerKeyMapping} relies on
 * {@code Minecraft.getInstance()} being available at entrypoint time, which is
 * not guaranteed on every launch path. Injecting here (when {@code Options} is
 * loaded) is deterministic and mirrors what Fabric's own {@code OptionsMixin}
 * does for the shared modded-keybind list.
 */
@Mixin(Options.class)
public abstract class RocketWithSpearOptionsMixin {

	@Shadow
	@Final
	@Mutable
	public KeyMapping[] keyMappings;

	@Inject(method = "load()V", at = @At("RETURN"))
	private void rocketwithspear$addKeybind(CallbackInfo ci) {
		KeyMapping key = RocketWithSpearClient.USE_ROCKET_KEY;
		for (KeyMapping existing : keyMappings) {
			if (existing == key || existing.getName().equals(key.getName())) {
				return;
			}
		}
		keyMappings = Arrays.copyOf(keyMappings, keyMappings.length + 1);
		keyMappings[keyMappings.length - 1] = key;
	}
}
