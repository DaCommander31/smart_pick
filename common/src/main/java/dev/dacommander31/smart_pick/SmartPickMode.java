package dev.dacommander31.smart_pick;

public enum SmartPickMode {
    Block,
    Tool,
    Hybrid;

    SmartPickMode() {}

    public static SmartPickMode next(SmartPickMode current) {
        SmartPickMode[] values = SmartPickMode.values();
        return values[(current.ordinal() + 1) % values.length];
    }
}
