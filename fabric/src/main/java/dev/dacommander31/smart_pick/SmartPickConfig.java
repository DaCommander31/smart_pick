package dev.dacommander31.smart_pick;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Excluded;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = SmartPickClient.MOD_ID)
public class SmartPickConfig implements ConfigData {
    public boolean enabled = true;
    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @Comment("""
            Changes Smart Pick's logic.\
            
            
            Block: Prioritizes matching block. If not found,\
            
                   will try to find a similar block.\
            
            
            Tool: Will give the tool used to mine the block picked.\
            
            
            Hybrid: Will use either Block or Tool mode depending on the inventory.\
            
                    If both block and tool are present:\
            
                        Not Crouching = Block Mode\
            
                        Crouching = Tool Mode
            """
    )
    public SmartPickMode mode = SmartPickMode.Block;
    @Comment("Prioritizes picking a silk touch\nenchanted tool in Tool mode.")
    public boolean prioritizeSilkTouch = false;
    public boolean actionbarMessages = true;
    public boolean debugLog = false;
    @Excluded
    public boolean showNoRpScreen = true;

    public SmartPickConfig() {}
}
