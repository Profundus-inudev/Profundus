package tech.inudev.profundus.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import tech.inudev.profundus.Profundus;
import tech.inudev.profundus.define.Metazon;
import tech.inudev.profundus.utils.DatabaseUtil;

import javax.xml.crypto.Data;
import java.util.Arrays;
import java.util.List;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args) {
        if (!command.getName().equalsIgnoreCase("test")) {
            return false;
        }

        if (args.length == 0) {
            new Metazon().open((Player) sender);
        } else if (args.length == 1) {
            List<DatabaseUtil.GoodsData> goodsList = DatabaseUtil.loadGoodsList();
            goodsList.forEach(v -> {
                ItemMeta meta = v.goods().getItemMeta();
                meta.lore(List.of(Component.text(v.price()), Component.text(v.seller())));
                v.goods().setItemMeta(meta);
                ((Player) sender).getInventory().addItem(v.goods());
            });
        } else if (args.length == 2) {
            ItemStack item = ((Player) sender).getItemInHand();
            StringBuilder builder = new StringBuilder();
            item.getEnchantments().keySet().forEach(key -> {
                builder.append(key.getKey())
                        .append(":")
                        .append(item.getEnchantments().get(key))
                        .append(",");
                ItemStack i = new ItemStack(Material.STONE_SWORD);
                i.addEnchantment(Enchantment.getByKey(NamespacedKey.fromString(key.getKey().toString())), 1);
                ((Player) sender).getInventory().addItem(i);
            });
            Profundus.getInstance().getLogger().info(
                    "" + item.getType() + "," + item.getAmount() + "," + builder);
        }
        return false;
    }
}