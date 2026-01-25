package dev.dacommander31.smart_pick;

import dev.dacommander31.smart_pick.platform.Platform;
import dev.dacommander31.smart_pick.util.PickBlockCache;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value = "smart_pick", dist = Dist.CLIENT)
@EventBusSubscriber(modid = "smart_pick", value = Dist.CLIENT)
public class SmartPickClient {
	public static final String MOD_ID = "smart_pick";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public SmartPickClient(ModContainer container) {
		container.registerConfig(ModConfig.Type.CLIENT, SmartPickConfig.SPEC);
		container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		Platform.init(new NeoForgePlatform());
	}

	@SubscribeEvent
	public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(new PickBlockCache.ReloadListener());
	}
}