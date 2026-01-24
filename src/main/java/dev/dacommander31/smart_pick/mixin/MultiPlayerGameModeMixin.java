package dev.dacommander31.smart_pick.mixin;

import dev.dacommander31.smart_pick.SmartPickClient;
import dev.dacommander31.smart_pick.SmartPickConfig;
import dev.dacommander31.smart_pick.util.PickBlockCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "handlePickItemFromBlock", at = @At("HEAD"), cancellable = true)
    public void handleSmartPickLogic(BlockPos blockPos, boolean bl, CallbackInfo ci) {
        SmartPickConfig config = SmartPickClient.getConfig();
        if (!config.enabled) return;

        ClientLevel level = minecraft.level;
        LocalPlayer player = minecraft.player;
        if (level == null || player == null) return;
        if (player.hasInfiniteMaterials()) return;
        Inventory inventory = player.getInventory();

        ItemStack targetBlockItem = level.getBlockState(blockPos).getCloneItemStack(level, blockPos, false);
        Map<Item, List<Item>> map = PickBlockCache.getCache();

        int slot = getMatchingSlot(targetBlockItem, map, inventory);
        if (slot == -1) return;

        if (config.actionbarMessages) {
            minecraft.gui.setOverlayMessage(Component.translatable("smart_pick.pick"), false);
        }

        if (Inventory.isHotbarSlot(slot)) {
            inventory.setSelectedSlot(slot);
        } else {
            inventory.pickSlot(slot);
            if (minecraft.gameMode != null) {
                SmartPickClient.log("Sending server packet");
                minecraft.gameMode.handleInventoryMouseClick(
                        player.containerMenu.containerId,
                        slot,
                        inventory.getSelectedSlot(),
                        ClickType.SWAP,
                        player
                );
            }
        }

        player.containerMenu.broadcastChanges();
        ci.cancel();
    }

    @Unique
    private int getMatchingSlot(ItemStack targetBlockItem, Map<Item, List<Item>> map, Inventory inventory) {
        for (Map.Entry<Item, List<Item>> entry : map.entrySet()) {
            Item targetItem = entry.getKey();

            if (!targetBlockItem.is(targetItem)) continue;

            for (Item item : entry.getValue()) {
                int slot = inventory.findSlotMatchingItem(new ItemStack(item));
                if (slot != -1) return slot;
            }

            return -1;
        }

        return -1;
    }


}
