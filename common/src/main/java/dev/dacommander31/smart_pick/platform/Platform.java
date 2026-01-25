package dev.dacommander31.smart_pick.platform;

public final class Platform {
    private static SmartPickPlatform INSTANCE;

    public static void init(SmartPickPlatform platform) {
        INSTANCE = platform;
    }

    public static SmartPickPlatform get() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Platform not initailized");
        }
        return INSTANCE;
    }
}
