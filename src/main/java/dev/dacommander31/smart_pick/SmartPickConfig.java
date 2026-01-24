package dev.dacommander31.smart_pick;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = SmartPickClient.MOD_ID)
public class SmartPickConfig implements ConfigData {
    public boolean enabled = true;
    public boolean actionbarMessages = true;
    public boolean debugLog = false;

    public SmartPickConfig() {}
}
