package dev.dacommander31.smart_pick.mixin;

import dev.dacommander31.smart_pick.platform.Platform;
import dev.dacommander31.smart_pick.platform.SmartPickPlatform;
import dev.dacommander31.smart_pick.util.PickBlockCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "handlePickItemFromBlock", at = @At("HEAD"), cancellable = true)
    public void handleSmartPickLogic(BlockPos blockPos, boolean bl, CallbackInfo ci) {
        SmartPickPlatform platform = Platform.get();
        if (!platform.isEnabled()) return;

        ClientLevel level = minecraft.level;
        LocalPlayer player = minecraft.player;
        if (level == null || player == null) return;

        BlockState blockState = level.getBlockState(blockPos);

        ItemStack targetBlockItem = blockState.getCloneItemStack(level, blockPos, false);

        Inventory inventory = player.getInventory();
        if (player.hasInfiniteMaterials()) return;
        int slot = -1;

        switch (platform.mode()) {
            case Block -> {
                if (inventory.contains(targetBlockItem)) return;
                Map<Item, List<Item>> map = PickBlockCache.getCache();
                slot = smart_pick$handleBlockPick(targetBlockItem, map, inventory, platform);
            }
            case Tool -> {
                slot = smart_pick$handleToolPick(inventory, blockState, player, platform);
                if (slot == -1) ci.cancel();
            }
            case Hybrid -> {
                if (player.isCrouching()) {
                    slot = smart_pick$handleToolPick(inventory, blockState, player, platform);

                    if (slot == -1) {
                        Map<Item, List<Item>> map = PickBlockCache.getCache();
                        slot = smart_pick$handleBlockPick(targetBlockItem, map, inventory, platform);
                    }
                } else {
                    if (inventory.contains(targetBlockItem)) return;
                    Map<Item, List<Item>> map = PickBlockCache.getCache();
                    slot = smart_pick$handleBlockPick(targetBlockItem, map, inventory, platform);

                    if (slot == -1) {
                        slot = smart_pick$handleToolPick(inventory, blockState, player, platform);
                    }
                }

            }
        }
        if (slot == -1) return;

        if (Inventory.isHotbarSlot(slot)) {
            inventory.setSelectedSlot(slot);
        } else {
            inventory.pickSlot(slot);
            if (minecraft.gameMode != null) {
                platform.log("Sending server packet");
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
    private int smart_pick$handleToolPick(Inventory inventory, BlockState blockState, LocalPlayer player, SmartPickPlatform platform) {
        int slot = -1;
        for (int i = 0; i < inventory.getNonEquipmentItems().size(); i++) {
            ItemStack stack = inventory.getItem(i);

            if (blockState.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                if (stack.is(ItemTags.PICKAXES)) {
                    if (platform.prioritizeSilkTouch() && !smart_pick$hasSilkTouch(stack, player) && inventory.hasAnyMatching(itemStack ->
                            itemStack.is(ItemTags.PICKAXES) && smart_pick$hasSilkTouch(itemStack, player))) continue;
                    slot = i;
                    break;
                }
            }

            if (blockState.is(BlockTags.MINEABLE_WITH_AXE)) {
                if (platform.prioritizeSilkTouch() && !smart_pick$hasSilkTouch(stack, player) && inventory.hasAnyMatching(itemStack ->
                        itemStack.is(ItemTags.AXES) && smart_pick$hasSilkTouch(itemStack, player))) continue;
                if (stack.is(ItemTags.AXES)) {
                    slot = i;
                    break;
                }
            }

            if (blockState.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
                if (stack.is(ItemTags.SHOVELS)) {
                    if (platform.prioritizeSilkTouch() && !smart_pick$hasSilkTouch(stack, player) && inventory.hasAnyMatching(itemStack ->
                            itemStack.is(ItemTags.SHOVELS) && smart_pick$hasSilkTouch(itemStack, player))) continue;
                    slot = i;
                    break;
                }
            }

            if (blockState.is(BlockTags.MINEABLE_WITH_HOE)) {
                if (stack.is(ItemTags.HOES)) {
                    if (platform.prioritizeSilkTouch() && !smart_pick$hasSilkTouch(stack, player) && inventory.hasAnyMatching(itemStack ->
                            itemStack.is(ItemTags.HOES) && smart_pick$hasSilkTouch(itemStack, player))) continue;
                    slot = i;
                    break;
                }
            }
            boolean swordEfficient =
                    blockState.is(BlockTags.SWORD_EFFICIENT) ||
                            blockState.is(BlockTags.SWORD_INSTANTLY_MINES);

            if (stack.is(Items.SHEARS) && smart_pick$isShearable(blockState, stack)) {
                if (!swordEfficient || inventory.hasAnyMatching(itemStack ->
                        itemStack.is(Items.SHEARS) && smart_pick$isShearable(blockState, itemStack))) {
                    slot = i;
                    break;
                }
            }

            if (swordEfficient && stack.is(ItemTags.SWORDS)) {
                slot = i;
                break;
            }

            if (stack.isCorrectToolForDrops(blockState)) {
                slot = i;
                break;
            }
        }
        if (slot != -1) {
            if (platform.actionbarMessages()) {
                minecraft.gui.setOverlayMessage(Component.translatable("smart_pick.pick_tool"), false);
            }
        }
        return slot;
    }

    @Unique
    private boolean smart_pick$isShearable(BlockState state, ItemStack stack) {
        Tool tool = stack.get(DataComponents.TOOL);
        if (tool == null) return false;

        for (Tool.Rule rule : tool.rules()) {
            if (rule.blocks().contains(state.getBlockHolder())) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private int smart_pick$handleBlockPick(ItemStack targetBlockItem, Map<Item, List<Item>> map, Inventory inventory, SmartPickPlatform platform) {
        for (Map.Entry<Item, List<Item>> entry : map.entrySet()) {
            Item targetItem = entry.getKey();

            if (!targetBlockItem.is(targetItem)) continue;

            for (Item item : entry.getValue()) {
                int slot = inventory.findSlotMatchingItem(new ItemStack(item));
                if (slot != -1 && platform.actionbarMessages()) {
                    minecraft.gui.setOverlayMessage(Component.translatable("smart_pick.pick_block"), false);
                }
                return slot;
            }

            return -1;
        }

        return -1;
    }

    @Unique
    private boolean smart_pick$hasSilkTouch(ItemStack stack, LocalPlayer player) {
        Optional<Holder.Reference<Enchantment>> silkTouchHolder = player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.SILK_TOUCH);
        return silkTouchHolder.filter(enchantmentReference -> EnchantmentHelper.getItemEnchantmentLevel(enchantmentReference, stack) > 0).isPresent();
    }

}
