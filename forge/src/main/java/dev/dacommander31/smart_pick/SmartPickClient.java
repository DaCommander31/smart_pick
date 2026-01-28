package dev.dacommander31.smart_pick;

import dev.dacommander31.smart_pick.platform.Platform;
import dev.dacommander31.smart_pick.util.PickBlockCache;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(SmartPickClient.MOD_ID)
@Mod.EventBusSubscriber(modid = SmartPickClient.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SmartPickClient {
	public static final String MOD_ID = "smart_pick";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@SuppressWarnings("removal")
	@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			AutoConfig.register(SmartPickConfig.class, Toml4jConfigSerializer::new);

			ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
					() -> new ConfigScreenHandler.ConfigScreenFactory(((minecraft, screen) ->
							AutoConfig.getConfigScreen(SmartPickConfig.class, screen).get())));

			Platform.init(new ForgePlatform());
		}
	}

	@SubscribeEvent
	public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(new PickBlockCache.ReloadListener());
	}
}