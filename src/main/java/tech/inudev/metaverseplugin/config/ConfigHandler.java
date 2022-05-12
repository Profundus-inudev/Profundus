package tech.inudev.metaverseplugin.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigHandler {

    private final Plugin plugin;
    private final FileConfiguration config;

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

    public int getConfigVersion() {
        return configVersion;
    }

    public void saveConfig() {
        plugin.saveConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
    }

}
