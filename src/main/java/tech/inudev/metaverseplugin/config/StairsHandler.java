package tech.inudev.metaverseplugin.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import tech.inudev.metaverseplugin.Metaverseplugin;

import java.io.File;
import java.util.List;

public class StairsHandler {
    private final Plugin plugin;
    private final FileConfiguration config;

    @Getter
    private List<String> stairList;

    public StairsHandler(Plugin plugin) {
        plugin.saveResource("stairs.yml", false);
        this.plugin = plugin;
        File stairsYml = new File(plugin.getDataFolder() + File.separator + "stairs.yml");
        this.config = YamlConfiguration.loadConfiguration(stairsYml);
        this.init();
    }

    private void init() {
        stairList = config.getStringList("stairs");
    }
}
