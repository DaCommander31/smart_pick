package dev.dacommander31.smart_pick.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import dev.dacommander31.smart_pick.SmartPickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

public class ModConfigScreenFactory implements ConfigScreenFactory<@NotNull Screen> {
    @Override
    public Screen create(Screen parent) {
        return AutoConfig.getConfigScreen(SmartPickConfig.class, parent).get();
    }
}
