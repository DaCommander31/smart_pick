package dev.dacommander31.smart_pick.mixin;

import dev.dacommander31.smart_pick.platform.Platform;
import dev.dacommander31.smart_pick.platform.SmartPickPlatform;
import dev.dacommander31.smart_pick.util.PickBlockCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Map;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow private static Minecraft instance;

    @Redirect(method = "pickBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;getCloneItemStack(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;"))
    public ItemStack handleSmartPickLogic(Block block, LevelReader level, BlockPos blockPos, BlockState blockState) {
        SmartPickPlatform platform = Platform.get();
        if (!platform.isEnabled()) return null;

        ItemStack targetBlockItem = block.getCloneItemStack(level, blockPos, blockState);

        LocalPlayer player = instance.player;
        assert player != null;
        Inventory inventory = player.getInventory();
        if (inventory.contains(targetBlockItem) || player.getAbilities().instabuild) return targetBlockItem;

        Map<Item, List<Item>> map = PickBlockCache.getCache();

        for (Map.Entry<Item, List<Item>> entry : map.entrySet()) {
            Item targetItem = entry.getKey();

            if (!targetBlockItem.is(targetItem)) continue;

            for (Item item : entry.getValue()) {
                if (inventory.contains(itemStack -> itemStack.is(item))) {
                    if (platform.actionbarMessages()) {
                        instance.gui.setOverlayMessage(Component.translatable("smart_pick.pick"), false);
                    }
                    return new ItemStack(item);
                }
            }
        }
        return targetBlockItem;
    }


}
