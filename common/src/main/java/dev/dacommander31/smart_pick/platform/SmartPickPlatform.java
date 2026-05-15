package dev.dacommander31.smart_pick.platform;

import dev.dacommander31.smart_pick.SmartPickMode;

public interface SmartPickPlatform {
    void log(String message, Object... objects);
    void error(String message, Object... objects);
    boolean isEnabled();
    SmartPickMode mode();
    boolean prioritizeSilkTouch();
    boolean actionbarMessages();
}
