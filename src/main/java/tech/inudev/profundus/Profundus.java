package tech.inudev.profundus;

import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import tech.inudev.profundus.config.ConfigHandler;
import tech.inudev.profundus.config.StairsHandler;
import tech.inudev.profundus.database.DatabaseUtil;
import tech.inudev.profundus.database.DatabaseUtil.Table;
import tech.inudev.profundus.define.Money;
import tech.inudev.profundus.listener.LoginEvent;
import tech.inudev.profundus.listener.StairSittingListener;
import tech.inudev.profundus.profundusLib.interfaces.JavaPluginWithConfigHandler;
import tech.inudev.profundus.profundusLib.scheduler.DatabasePingRunnable;
import tech.inudev.profundus.profundusLib.utils.HelpUtil;
import tech.inudev.profundus.utils.StairSittingUtil;

/**
 * メタバースプラグイン（仮）
 *
 * @author kumitatepazuru, tererun, toru-toruto
 */
public final class Profundus extends JavaPluginWithConfigHandler {

    @Getter
    private static Profundus instance;

    @Getter
    private ConfigHandler configHandler;
    @Getter
    private StairsHandler stairsHandler;
    private DatabasePingRunnable databasePingRunnable;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        this.configHandler = new ConfigHandler(instance);
        this.stairsHandler = new StairsHandler(instance);
        this.databasePingRunnable = new DatabasePingRunnable();

        DatabaseUtil.init(this);
        DatabaseUtil.connect();
        for (Table table : Table.values()) {
            //ここの第二引数をtrueにすると，テーブル再作成（データ消える）
            //TODO リリース時には第二引数は削除
            DatabaseUtil.createTable(table, false);
        }

        if (!Money.bankAccountExists(this.configHandler.getMasterBankName())) {
            Money.createBankAccount(this.configHandler.getMasterBankName());
        }

        registerSchedulers();
        registerListeners();

        HelpUtil.initializeHelp(this);

        getLogger().info("Profundus plugin enabled!");
    }

    private void registerSchedulers() {
        this.databasePingRunnable.runTaskTimer(this, 0, 20 * 60 * 60);
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new StairSittingListener(), this);
        pm.registerEvents(new LoginEvent(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        StairSittingUtil.removeSeatsOnServerDisable();

        DatabaseUtil.disconnect();

        getLogger().info("Profundus plugin disabled!");
    }
}
