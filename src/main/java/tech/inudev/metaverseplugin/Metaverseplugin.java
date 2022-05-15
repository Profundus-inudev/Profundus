package tech.inudev.metaverseplugin;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import tech.inudev.metaverseplugin.config.ConfigHandler;
import tech.inudev.metaverseplugin.scheduler.DatabasePingRunnable;
import tech.inudev.metaverseplugin.utils.DatabaseUtil;

import java.util.logging.Logger;

public final class Metaverseplugin extends JavaPlugin {

    @Getter private static Logger logger;
    @Getter private static Metaverseplugin instance;

    @Getter private ConfigHandler configHandler;
    private DatabasePingRunnable databasePingRunnable;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        logger = getLogger();

        this.configHandler = new ConfigHandler(instance);
        this.databasePingRunnable = new DatabasePingRunnable();

        DatabaseUtil.connect();

        registerSchedulers();

        logger.info("Metaverseplugin enabled!");
    }

    private void registerSchedulers() {
        this.databasePingRunnable.runTaskTimer(this, 0, 20 * 60 * 60);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        DatabaseUtil.disconnect();

        logger.info("Metaverseplugin disabled!");
    }

}
