package dev.dacommander31.smart_pick.util;

import com.google.gson.*;
import dev.dacommander31.smart_pick.SmartPickClient;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ClientBlockTagParser {

    public static List<Item> parse(Resource resource, Set<String> visited) {
        String id = resource.sourcePackId();

        if (!visited.add(id)) {
            return List.of();
        }

        try (InputStream stream = resource.open()) {
            JsonObject obj = JsonParser
                    .parseReader(new InputStreamReader(stream))
                    .getAsJsonObject();

            JsonArray values = obj.getAsJsonArray("values");
            return parseEntries(values, visited);

        } catch (IOException e) {
            SmartPickClient.LOGGER.error(
                    "Failed to read client block tag at {}", id, e
            );
            return List.of();
        }
    }


    private static List<Item> parseEntries(JsonArray block, Set<String> visited) {
        Set<Item> entries = new LinkedHashSet<>();
        ResourceManager rm = Minecraft.getInstance().getResourceManager();

        for (JsonElement el : block) {
            String id = el.getAsString();

            if (id.startsWith("#")) {
                TagKey<Block> tag = PickBlockMapParser.createTagKey(id);
                entries.addAll(PickBlockMapParser.getItems(tag));

            } else if (id.startsWith("$")) {
                Identifier ref = Identifier.parse(id.substring(1));

                Identifier resourceId = Identifier.fromNamespaceAndPath(
                        ref.getNamespace(),
                        "smart_pick/block_tags/" + ref.getPath() + ".json"
                );

                rm.getResource(resourceId).ifPresent(res ->
                        entries.addAll(parse(res, visited))
                );

            } else {
                Item item = PickBlockMapParser.getItem(id);
                if (item != Items.AIR) {
                    entries.add(item);
                }
            }
        }

        return List.copyOf(entries);
    }

}
