package dev.dacommander31.smart_pick;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SmartPickConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER.define("enabled", true);
    public static final ModConfigSpec.BooleanValue ACTIONBAR_MESSAGES = BUILDER.define("actionbarMessages", true);
    public static final ModConfigSpec.BooleanValue DEBUG_LOG = BUILDER.define("debugLog", false);

    static final ModConfigSpec SPEC = BUILDER.build();
}
