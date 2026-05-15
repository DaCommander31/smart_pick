package dev.dacommander31.smart_pick;

import dev.dacommander31.smart_pick.platform.SmartPickPlatform;

public class FabricPlatform implements SmartPickPlatform {
    @Override
    public void log(String message, Object... objects) {
        if (SmartPickClient.getConfig().debugLog) {
            SmartPickClient.LOGGER.info(message, objects);
        }
    }

    @Override
    public void error(String message, Object... objects) {
        SmartPickClient.LOGGER.error(message, objects);
    }

    @Override
    public boolean isEnabled() {
        return SmartPickClient.getConfig().enabled;
    }

    @Override
    public SmartPickMode mode() {
        return SmartPickClient.getConfig().mode;
    }

    @Override
    public boolean prioritizeSilkTouch() {
        return SmartPickClient.getConfig().prioritizeSilkTouch;
    }

    @Override
    public boolean actionbarMessages() {
        return SmartPickClient.getConfig().actionbarMessages;
    }
}
