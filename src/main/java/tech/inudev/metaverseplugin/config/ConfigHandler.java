package tech.inudev.metaverseplugin.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import tech.inudev.metaverseplugin.Metaverseplugin;

import java.util.ArrayList;
import java.util.List;

/**
 * config.ymlファイルを扱いやすくするために作られたHandler
 *
 * @author tererun
 */
public class ConfigHandler {

    private final Plugin plugin;
    private final FileConfiguration config;

    @Getter private int configVersion;
    @Getter private String databaseAddress;
    @Getter private String databaseName;
    @Getter private String databaseUsername;
    @Getter private String databasePassword;
    @Getter private String databaseType;

    @Getter private List<String> priceTypes;

    /**
     * コンストラクタ
     *
     * @param plugin プラグイン
     */
    public ConfigHandler(Plugin plugin) {
        plugin.saveDefaultConfig();
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.init();
    }

    private void init() {
        configVersion = config.getInt("config_version");

        String databasePath = "database.";
        databaseType = config.getString(databasePath + "type");
        databaseAddress = config.getString(databasePath + "address");
        databaseName = config.getString(databasePath + "database");
        databaseUsername = config.getString(databasePath + "username");
        databasePassword = config.getString(databasePath + "password");

        priceTypes = new ArrayList<>(
                config.getConfigurationSection("prices").getKeys(false));
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


    public Integer getBasicPrice(String type) {
        String path = "prices." + type;
        return config.isInt(path) ? config.getInt(path) : null;
    }
}
