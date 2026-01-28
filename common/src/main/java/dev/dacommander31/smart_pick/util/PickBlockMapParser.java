package dev.dacommander31.smart_pick.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.dacommander31.smart_pick.platform.Platform;
import dev.dacommander31.smart_pick.platform.SmartPickPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PickBlockMapParser {
    public static Map<Item, List<Item>> parse(JsonObject obj) {
        Map<Item, List<Item>> map = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (value instanceof JsonPrimitive primitive && primitive.isString()) {
                JsonArray array = new JsonArray();
                array.add(primitive);
                value = array;
            }

            if (!(value instanceof JsonArray block)) {
                continue;
            }

            SmartPickPlatform platform = Platform.get();

            if (key.startsWith("#")) {
                platform.log("Recognised block tag {} containing {}", key, Arrays.toString(getItems(createTagKey(key)).toArray()));
                for (Item keyItem : getItems(createTagKey(key))) {
                    List<Item> blockItemEntries = parseEntries(block, keyItem, null);
                    platform.log("Mapping item {} with {}", keyItem.toString(), Arrays.toString(blockItemEntries.toArray()));
                    map.putIfAbsent(keyItem, blockItemEntries);
                }
            } else if (key.startsWith("$")) {
                ResourceLocation id = new ResourceLocation(key.substring(1));
                ResourceManager rm = Minecraft.getInstance().getResourceManager();

                ResourceLocation resourceId = new ResourceLocation(
                        id.getNamespace(),
                        "smart_pick/block_tags/" + id.getPath() + ".json"
                );

                rm.getResource(resourceId).ifPresent(resource -> {
                    List<Item> keyItems = ClientBlockTagParser.parse(resource, new HashSet<>());

                    platform.log(
                            "Recognised client block tag {} containing {}",
                            key, keyItems
                    );

                    for (Item keyItem : keyItems) {
                        List<Item> blockItemEntries = parseEntries(block, keyItem, resource);
                        map.putIfAbsent(keyItem, blockItemEntries);
                    }
                });

            } else {
                Item keyItem = getItem(key);
                List<Item> blockItemEntries = parseEntries(block, keyItem, null);
                platform.log("Mapping item {} with {}", keyItem, Arrays.toString(blockItemEntries.toArray()));
                map.put(keyItem, blockItemEntries);
            }

        }
        return map;
    }

    private static List<Item> parseEntries(JsonArray block, Item keyItem, @Nullable Resource resource) {
        Set<Item> entries = new LinkedHashSet<>();

        for (JsonElement el : block) {
            String id = el.getAsString();

            if (id.startsWith("#")) {
                TagKey<Block> tag = createTagKey(id);
                for (Item item : getItems(tag)) {
                    if (keyItem != null && BuiltInRegistries.ITEM.getKey(item).equals(BuiltInRegistries.ITEM.getKey(keyItem))) {
                        continue;
                    }
                    entries.add(item);
                }
            } else if (id.startsWith("$")) {
                assert resource != null;
                entries.addAll(ClientBlockTagParser.parse(resource, new HashSet<>()));
            } else {
                Item item = getItem(id);
                if (item != Items.AIR && !item.equals(keyItem)) {
                    entries.add(item);
                }
            }
        }

        return List.copyOf(entries);
    }

    public static Item getItem(String id) {
        return BuiltInRegistries.ITEM.get(new ResourceLocation(id));
    }

    public static List<Item> getItems(TagKey<Block> blockTag) {
        Iterable<Holder<Block>> blocks = BuiltInRegistries.BLOCK.getTagOrEmpty(blockTag);

        List<Item> toReturn = new ArrayList<>();
        for (Holder<Block> blockEntry : blocks) {
            if (blockEntry.value() != Blocks.AIR) {
                toReturn.add(blockEntry.value().asItem());
            }
        }
        return toReturn;
    }

    public static TagKey<Block> createTagKey(String string) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(string.substring(1)));
    }
}
