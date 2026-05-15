package dev.dacommander31.smart_pick;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

public class SmartPickConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER.define("enabled", true);
    public static final ModConfigSpec.EnumValue<@NotNull SmartPickMode> MODE = BUILDER.comment("""
            Changes Smart Pick's logic.\
            
            
            Block: Prioritizes matching block. If not found,\
            
                   will try to find a similar block.\
            
            
            Tool: Will give the tool used to mine the block picked.\
            
            
            Hybrid: Will use either Block or Tool mode depending on the inventory.\
            
                    If both block and tool are present:\
            
                        Not Crouching = Block Mode\
            
                        Crouching = Tool Mode
            """).defineEnum("mode", SmartPickMode.Block);
    public static final ModConfigSpec.BooleanValue PRIORITIZE_SILK_TOUCH = BUILDER.define("prioritizeSilkTouch", false);
    public static final ModConfigSpec.BooleanValue ACTIONBAR_MESSAGES = BUILDER.define("actionbarMessages", true);
    public static final ModConfigSpec.BooleanValue DEBUG_LOG = BUILDER.define("debugLog", false);

    static final ModConfigSpec SPEC = BUILDER.build();
}
