package tech.inudev.profundus.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import tech.inudev.profundus.Profundus;

import java.awt.print.Book;
import java.util.UUID;

public class HelpUtil {
    public static void openHelp(UUID playerUUID, int helpId) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null && player.isOnline()) {
            ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
            if (helpId == 0) {
                BookMeta bookMeta = ((BookMeta) writtenBook.getItemMeta())
                        .author(Component.text("Master"))
                        .title(Component.text("Help"));
                bookMeta.addPages(Component.text("あじたま"));
                writtenBook.setItemMeta(bookMeta);
            }
            player.openBook(writtenBook);
        }
    }
}
