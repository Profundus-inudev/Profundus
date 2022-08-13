package tech.inudev.profundus.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import tech.inudev.profundus.profundusLib.config.BaseConfigHandler;

/**
 * config.ymlファイルを扱いやすくするために作られたHandler
 *
 * @author tererun
 */
public class ConfigHandler extends BaseConfigHandler {

    @Getter
    private String masterBankName;

    public ConfigHandler(Plugin plugin) {
        super(plugin);
    }

    protected void init() {
        String databasePath = "database.";

        masterBankName = config.getString(databasePath + "master_bank_name");
    }
}
