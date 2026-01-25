package dev.dacommander31.smart_pick.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.dacommander31.smart_pick.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickBlockCache {
    private static final Map<Item, List<Item>> CACHE = new HashMap<>();

    public static Map<Item, List<Item>> getCache() {
        if (!CACHE.isEmpty()) return CACHE;

        ResourceManager rm = Minecraft.getInstance().getResourceManager();

        rm.listResources(
                "smart_pick",
                path -> path.getPath().endsWith("block_pick_map.json")
        ).forEach((id, resource) -> load(resource, id));

        return CACHE;
    }


    private static void load(Resource resource, Identifier id) {
        try (InputStreamReader reader =
                     new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {

            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            Platform.get().log("Successfully loaded block pick map for {}", id);
            CACHE.putAll(PickBlockMapParser.parse(obj));

        } catch (Exception e) {
            Platform.get().error(
                    "Failed to load Smart Pick map from {}", id, e
            );
        }
    }

    public static class ReloadListener extends SimplePreparableReloadListener<Void> {

        @Override
        protected Void prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            return null;
        }

        @Override
        protected void apply(Void object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            Platform.get().log("Clearing block pick cache.");
            CACHE.clear();
        }
    }

}
