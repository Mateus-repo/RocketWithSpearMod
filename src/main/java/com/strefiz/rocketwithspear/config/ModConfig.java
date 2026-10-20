package com.strefiz.rocketwithspear.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Mod configuration persisted as JSON in the Minecraft {@code config/} folder.
 *
 * <p>Loaded lazily and kept in memory; the config screen mutates the singleton
 * and calls {@link #save()} on every change.
 */
public class ModConfig {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File CONFIG_FILE = new File(
			FabricLoader.getInstance().getConfigDir().toFile(), "rocketwithspear.json");

	private static ModConfig INSTANCE;

	/** Master switch for the keybind. */
	public boolean enabled = true;
	/** Preferred hand for the rocket: {@code OFF_HAND} or {@code ANY}. */
	public String preferredHand = "OFF_HAND";
	/** Only work while gliding with an elytra. */
	public boolean requireFlying = true;
	/** Only work while the trident/spear is being charged. */
	public boolean requireSpearCharging = true;
	/** Local anti-spam cooldown, in ticks. */
	public int cooldownTicks = 5;
	/** Use the custom server packet when the server also has the mod. */
	public boolean allowServerMod = true;

	public static ModConfig get() {
		if (INSTANCE == null) {
			INSTANCE = load();
		}
		return INSTANCE;
	}

	public static ModConfig load() {
		if (CONFIG_FILE.exists()) {
			try (FileReader reader = new FileReader(CONFIG_FILE)) {
				ModConfig config = GSON.fromJson(reader, ModConfig.class);
				if (config == null) {
					config = new ModConfig();
				}
				config.save();
				return config;
			} catch (IOException | RuntimeException e) {
				// Fall through to defaults on read/parse errors.
			}
		}
		ModConfig config = new ModConfig();
		config.save();
		return config;
	}

	public void save() {
		try {
			CONFIG_FILE.getParentFile().mkdirs();
			try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException e) {
			// Ignore write errors (read-only config folder, etc.).
		}
	}

	public boolean useOffHandPreferred() {
		return "OFF_HAND".equalsIgnoreCase(preferredHand);
	}
}
