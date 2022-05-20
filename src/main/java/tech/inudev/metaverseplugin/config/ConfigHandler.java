package tech.inudev.metaverseplugin.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import tech.inudev.metaverseplugin.Metaverseplugin;

import java.util.HashMap;

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

    @Getter private HashMap<String, Integer> priceMap;

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

        String pricesSection = "prices";
        priceMap = new HashMap<>();
        for (String type : config.getConfigurationSection(pricesSection).getKeys(false)) {
            priceMap.put(type, config.getInt(pricesSection + "." + type));
        }
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

    /**
     * 販売商品の基本価格を取得する
     *
     * @param type 商品タイプ
     * @return 基本価格
     */
    public Integer getBasicPrice(String type) {
        return priceMap.get(type);
    }
}
