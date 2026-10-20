package com.strefiz.rocketwithspear.config;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Native Minecraft config screen shown from Mod Menu. Saves the config on every
 * change and returns to the parent screen with "Done". The base {@link Screen}
 * renders the background and widgets.
 */
public class ModConfigScreen extends Screen {

	private static final int BUTTON_WIDTH = 240;
	private static final int BUTTON_HEIGHT = 20;

	private final Screen parent;
	private final ModConfig config;

	public ModConfigScreen(Screen parent) {
		super(Component.translatable("config.rocketwithspear.title"));
		this.parent = parent;
		this.config = ModConfig.get();
	}

	@Override
	protected void init() {
		int x = this.width / 2 - BUTTON_WIDTH / 2;
		int y = this.height / 6;

		addToggle(x, y, this::enabledLabel, () -> config.enabled, value -> config.enabled = value);
		y += 22;
		addToggle(x, y, this::flyingLabel, () -> config.requireFlying,
				value -> config.requireFlying = value);
		y += 22;
		addToggle(x, y, this::spearLabel, () -> config.requireSpearCharging,
				value -> config.requireSpearCharging = value);
		y += 22;
		addToggle(x, y, this::serverLabel, () -> config.allowServerMod,
				value -> config.allowServerMod = value);
		y += 22;

		this.addRenderableWidget(Button.builder(this.handLabel(), button -> {
			config.preferredHand = config.useOffHandPreferred() ? "ANY" : "OFF_HAND";
			config.save();
			button.setMessage(this.handLabel());
		}).bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
		y += 32;

		this.addRenderableWidget(Button.builder(Component.translatable("config.rocketwithspear.done"),
				button -> Minecraft.getInstance().setScreenAndShow(this.parent))
				.bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
	}

	/** Adds a toggle button, reading the live value and persisting changes. */
	private void addToggle(int x, int y, Supplier<Component> label, Supplier<Boolean> getter,
			Consumer<Boolean> setter) {
		this.addRenderableWidget(Button.builder(label.get(), button -> {
			boolean newValue = !getter.get();
			setter.accept(newValue);
			config.save();
			button.setMessage(label.get());
		}).bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
	}

	private Component enabledLabel() {
		return Component.translatable("config.rocketwithspear.enabled", onOff(config.enabled));
	}

	private Component flyingLabel() {
		return Component.translatable("config.rocketwithspear.requireFlying",
				onOff(config.requireFlying));
	}

	private Component spearLabel() {
		return Component.translatable("config.rocketwithspear.requireSpear",
				onOff(config.requireSpearCharging));
	}

	private Component serverLabel() {
		return Component.translatable("config.rocketwithspear.allowServer",
				onOff(config.allowServerMod));
	}

	private Component handLabel() {
		return Component.translatable("config.rocketwithspear.hand",
				Component.literal(config.preferredHand));
	}

	private static Component onOff(boolean value) {
		return Component.translatable(value ? "config.rocketwithspear.on"
				: "config.rocketwithspear.off");
	}

	@Override
	public void onClose() {
		config.save();
		Minecraft.getInstance().setScreenAndShow(this.parent);
	}
}
