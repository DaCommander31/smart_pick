package dev.dacommander31.smart_pick.mixin;

import dev.dacommander31.smart_pick.platform.Platform;
import dev.dacommander31.smart_pick.util.PickBlockCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.Map;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    static Minecraft instance;

    @Shadow @Nullable
    public LocalPlayer player;

    @ModifyVariable(
            method = "pickBlock",
            at = @At(
                    value = "STORE"
            ),
            ordinal = 0
    )
    private ItemStack smartPick$modifyPickedStack(ItemStack stack) {
        assert player != null;
        Inventory inventory = player.getInventory();
        if (stack.isEmpty() || inventory.contains(stack) || player.getAbilities().instabuild) return stack;

        Map<Item, List<Item>> map = PickBlockCache.getCache();

        for (Map.Entry<Item, List<Item>> entry : map.entrySet()) {
            Item targetItem = entry.getKey();

            if (!stack.is(targetItem)) continue;


            for (Item item : entry.getValue()) {
                if (player.getInventory().hasAnyMatching(itemStack -> itemStack.is(item))) {
                    if (Platform.get().actionbarMessages()) {
                        instance.gui.setOverlayMessage(Component.translatable("smart_pick.pick"), false);
                    }
                    return new ItemStack(item);
                }
            }
        }

        return stack;
    }
}
