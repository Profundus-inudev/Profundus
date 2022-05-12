package tech.inudev.metaverseplugin;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import tech.inudev.metaverseplugin.config.ConfigHandler;
import tech.inudev.metaverseplugin.utils.DatabaseUtil;

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

        DatabaseUtil.connect();

        logger.info("Metaverseplugin enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        DatabaseUtil.disconnect();

        logger.info("Metaverseplugin disabled!");
    }

}
