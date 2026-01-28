package dev.dacommander31.smart_pick;

import dev.dacommander31.smart_pick.platform.SmartPickPlatform;
import me.shedaniel.autoconfig.AutoConfig;

public class ForgePlatform implements SmartPickPlatform {
    private static final SmartPickConfig config = AutoConfig.getConfigHolder(SmartPickConfig.class).getConfig();
    @Override
    public void log(String message, Object... objects) {
        if (config.debugLog) {
            SmartPickClient.LOGGER.info(message, objects);
        }
    }

    @Override
    public void error(String message, Object... objects) {
        SmartPickClient.LOGGER.error(message, objects);
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }

    @Override
    public boolean actionbarMessages() {
        return config.actionbarMessages;
    }
}
