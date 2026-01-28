package dev.dacommander31.smart_pick;

import dev.dacommander31.smart_pick.platform.Platform;
import dev.dacommander31.smart_pick.util.PickBlockCache;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartPickClient implements ClientModInitializer {
	public static final String MOD_ID = "smart_pick";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ReloadListener());

		AutoConfig.register(SmartPickConfig.class, Toml4jConfigSerializer::new);

		Platform.init(new FabricPlatform());
	}

	public static SmartPickConfig getConfig() {
		return AutoConfig.getConfigHolder(SmartPickConfig.class).getConfig();
	}

	private static class ReloadListener implements SimpleSynchronousResourceReloadListener {

		@Override
		public ResourceLocation getFabricId() {
			return new ResourceLocation(MOD_ID, "clear_bp_cache");
		}

		@Override
		public void onResourceManagerReload(ResourceManager resourceManager) {
			Platform.get().log("Clearing block pick cache.");
			PickBlockCache.clear();
		}
	}
}