package tech.inudev.metaverseplugin.listener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import tech.inudev.metaverseplugin.Metaverseplugin;
import tech.inudev.metaverseplugin.define.Money;
import tech.inudev.metaverseplugin.utils.PriceUtil;

public class TestPriceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args) {
        if (!command.getName().equalsIgnoreCase("price")) {
            return false;
        }

        if (args.length == 1) {
            int price = PriceUtil.getPrice(args[0]);
            Metaverseplugin.getInstance().getLogger().info("price: " + price);
            return true;
        } else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("add")) {
                Metaverseplugin.getInstance().getLogger().info("add");
                Metaverseplugin.getInstance().getLogger().info("" + Integer.parseInt(args[2]));
                PriceUtil.addPrice(args[0], Integer.parseInt(args[2]));
            } else if (args[1].equalsIgnoreCase("sub")) {
                Metaverseplugin.getInstance().getLogger().info("sub");
                PriceUtil.substructPrice(args[0], Integer.parseInt(args[2]));
            } else if (args[1].equalsIgnoreCase("set")) {
                Metaverseplugin.getInstance().getLogger().info("set");
                PriceUtil.setPrice(args[0], Integer.parseInt(args[2]));
            }
            int price = PriceUtil.getPrice(args[0]);
            Metaverseplugin.getInstance().getLogger().info("price: " + price);
            return true;
        }
        return false;
    }
}