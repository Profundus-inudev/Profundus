package tech.inudev.metaverseplugin;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import tech.inudev.metaverseplugin.config.ConfigHandler;

import java.util.logging.Logger;

public final class Metaverseplugin extends JavaPlugin {
    @Getter
    private static Logger logger;
    private static Metaverseplugin instance;

    private ConfigHandler configHandler;

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

    /**
     * メインクラスをインスタンスを取得する
     * @return メインクラスのインスタンス
     */
    public static Metaverseplugin getInstance() {
        return instance;
    }

    /**
     * ConfigHandlerのインスタンスを取得する
     * @return ConfigHandlerのインスタンス
     */
    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

}
