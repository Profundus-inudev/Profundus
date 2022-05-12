package tech.inudev.metaverseplugin;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import tech.inudev.metaverseplugin.config.ConfigHandler;

import java.util.logging.Logger;

public final class Metaverseplugin extends JavaPlugin {

    @Getter private static Logger logger;
    @Getter private static Metaverseplugin instance;

    @Getter private ConfigHandler configHandler;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        logger = getLogger();

        this.configHandler = new ConfigHandler(instance);

        logger.info("Metaverseplugin enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        logger.info("Metaverseplugin disabled!");
    }

}
