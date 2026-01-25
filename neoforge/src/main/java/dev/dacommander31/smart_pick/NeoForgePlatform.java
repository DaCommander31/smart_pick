package dev.dacommander31.smart_pick;

import dev.dacommander31.smart_pick.platform.SmartPickPlatform;

public class NeoForgePlatform implements SmartPickPlatform {
    @Override
    public void log(String message, Object... objects) {
        if (!SmartPickConfig.SPEC.isLoaded()) return;
        if (SmartPickConfig.DEBUG_LOG.isTrue()) {
            SmartPickClient.LOGGER.info(message, objects);
        }
    }

    @Override
    public void error(String message, Object... objects) {
        SmartPickClient.LOGGER.error(message, objects);
    }

    @Override
    public boolean isEnabled() {
        if (!SmartPickConfig.SPEC.isLoaded()) return true;
        return SmartPickConfig.ENABLED.getAsBoolean();
    }

    @Override
    public boolean actionbarMessages() {
        if (!SmartPickConfig.SPEC.isLoaded()) return true;
        return SmartPickConfig.ACTIONBAR_MESSAGES.getAsBoolean();
    }
}
