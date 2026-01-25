package dev.dacommander31.smart_pick;

import dev.dacommander31.smart_pick.platform.Platform;
import dev.dacommander31.smart_pick.util.PickBlockCache;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartPickClient implements ClientModInitializer {
	public static final String MOD_ID = "smart_pick";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(ResourceLocation.fromNamespaceAndPath(MOD_ID, "clear_bp_cache"), new PickBlockCache.ReloadListener());

		AutoConfig.register(SmartPickConfig.class, Toml4jConfigSerializer::new);

		Platform.init(new FabricPlatform());
	}

	public static SmartPickConfig getConfig() {
		return AutoConfig.getConfigHolder(SmartPickConfig.class).getConfig();
	}
}