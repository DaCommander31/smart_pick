package dev.dacommander31.smart_pick;

import com.mojang.blaze3d.platform.InputConstants;
import dev.dacommander31.smart_pick.platform.Platform;
import dev.dacommander31.smart_pick.util.PickBlockCache;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartPickClient implements ClientModInitializer {
	public static final String MOD_ID = "smart_pick";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static boolean ranFirstTick = false;

	private static final String RP_URL = "https://modrinth.com/project/smart-pick-resource-pack";


	@Override
	public void onInitializeClient() {
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(Identifier.fromNamespaceAndPath(MOD_ID, "clear_bp_cache"), new PickBlockCache.ReloadListener());

		AutoConfig.register(SmartPickConfig.class, Toml4jConfigSerializer::new);

		KeyMapping changeModeKeybind = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.smart_pick.mode", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, KeyMapping.Category.GAMEPLAY));

		Platform.init(new FabricPlatform());

		ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
			LocalPlayer player = minecraft.player;
			if (player != null) {
				if (changeModeKeybind.consumeClick()) {
					player.playSound(SoundEvents.UI_BUTTON_CLICK.value());
					SmartPickMode newMode = SmartPickMode.next(getConfig().mode);

					getConfig().mode = newMode;
					minecraft.gui.setOverlayMessage(Component.translatable("smart_pick.change_mode", newMode.name()), false);
				}
			}
		});
		ScreenEvents.AFTER_INIT.register((minecraft, screen, i, i1) -> {
			if (ranFirstTick) return;
			if (minecraft.isGameLoadFinished()) {
				ranFirstTick = true;
				if (getConfig().showNoRpScreen) {
					openConfirmFlow(minecraft, minecraft.screen);
				}
			}
		});

	}

	public void openConfirmFlow(Minecraft minecraft, Screen parent) {

		final ConfirmScreen[] firstConfirm = new ConfirmScreen[1];

		firstConfirm[0] = new ConfirmScreen(result -> {
			if (result) {
				minecraft.setScreen(new ConfirmLinkScreen(open -> {
					if (open) {
						Util.getPlatform().openUri(RP_URL);
						minecraft.setScreen(parent);
					} else {
						minecraft.setScreen(firstConfirm[0]);
					}
				}, RP_URL, true));
			} else {
				minecraft.setScreen(parent);
			}
		},
				Component.translatable("smart_pick_resource_pack_not_found_screen.title"),
				Component.translatable("smart_pick_resource_pack_not_found_screen.prompt")
		);

		minecraft.setScreen(firstConfirm[0]);
	}

	public static SmartPickConfig getConfig() {
		return AutoConfig.getConfigHolder(SmartPickConfig.class).getConfig();
	}

}