package com.strefiz.rocketwithspear;

import com.strefiz.rocketwithspear.network.ModNetworking;

import net.fabricmc.api.ModInitializer;

/**
 * Common entrypoint. Only used to register the optional server-side packet
 * handling. The mod adds no items, blocks, entities or registries, so vanilla
 * clients and servers can still join/serve without it.
 */
public class RocketWithSpearMod implements ModInitializer {

	public static final String MOD_ID = "rocketwithspear";

	@Override
	public void onInitialize() {
		ModNetworking.register();
	}
}
