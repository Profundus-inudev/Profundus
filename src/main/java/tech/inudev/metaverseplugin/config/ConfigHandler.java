package tech.inudev.metaverseplugin.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * config.ymlファイルを扱いやすくするために作られたHandler
 *
 * @author tererun
 */
public class ConfigHandler {

    private final Plugin plugin;
    private final FileConfiguration config;

    @Getter
    private int configVersion;

    public ConfigHandler(Plugin plugin) {
        plugin.saveDefaultConfig();
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.init();
    }

    private void init() {
        configVersion = config.getInt("config_version");
    }

    /**
     * コンフィグを保存する
     */
    public void saveConfig() {
        plugin.saveConfig();
    }

    /**
     * コンフィグをリロードする
     */
    public void reloadConfig() {
        plugin.reloadConfig();
    }

}
