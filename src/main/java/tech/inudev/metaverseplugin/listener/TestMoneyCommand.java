package tech.inudev.metaverseplugin.listener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import tech.inudev.metaverseplugin.Metaverseplugin;
import tech.inudev.metaverseplugin.define.Money;

public class TestMoneyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args) {
        if (!command.getName().equalsIgnoreCase("money")) {
            return false;
        }

        if (args.length == 0) {
            Money money = new Money("");
            Metaverseplugin.getInstance().getLogger().info("hoge" + money.getAmount());
            money.add(50);
            money.remove(50);
            Metaverseplugin.getInstance().getLogger().info("piyo" + money.getAmount());
            money.push();
            Metaverseplugin.getInstance().getLogger().info("fuga" + money.getAmount());
        } else if (args.length == 1) {
            Money money = new Money(args[0]);
            Metaverseplugin.getInstance().getLogger().info("" + money.getAmount());
            return true;
        } else if (args.length == 2) {
            Money money = new Money(args[0]);
            money.add(Integer.parseInt(args[1]) + 50);
            money.remove(50);
            money.push();
            Metaverseplugin.getInstance().getLogger().info("" + money.getAmount());
            return true;
        }
        return false;
    }
}
