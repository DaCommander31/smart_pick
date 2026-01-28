package dev.dacommander31.smart_pick.platform;

public interface SmartPickPlatform {
    void log(String message, Object... objects);
    void error(String message, Object... objects);
    boolean isEnabled();
    boolean actionbarMessages();
}
