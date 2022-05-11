package tech.inudev.metaverseplugin;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Metaverseplugin extends JavaPlugin {
    @Getter
    private static Logger logger;

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();

        logger.info("Metaverseplugin enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        logger.info("Metaverseplugin disabled!");
    }
}
