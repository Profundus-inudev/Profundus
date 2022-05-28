package tech.inudev.metaverseplugin.define;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import tech.inudev.metaverseplugin.Metaverseplugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * GUIを簡単に作れるようになるクラス。
 *
 * @author kumitatepazuru
 */
public class Gui implements Listener {
    record PosMenuItem(MenuItem menuItem, int x, int y){
    }

    @Getter
    private final List<PosMenuItem> menuItems = new ArrayList<>();
    @Getter
    @Setter
    private String title;
    @Getter
    private Inventory inventory;

    /**
     * コンストラクタ
     *
     * @param title GUIのタイトル
     */
    public Gui(String title) {
        this.title = title;
    }

    /**
     * GUIにアイテムを追加する。
     *
     * @param menuItem 追加するアイテム
     */
    public void addItem(MenuItem menuItem,int x,int y) {
        menuItems.add(new PosMenuItem(menuItem,x,y));
    }

    /**
     * GUIを開く。
     *
     * @param player GUIを開くプレイヤー
     */
    public void open(Player player) {
        if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
            openBedrockImpl(player);
        } else {
            openJavaImpl(player);
        }
    }

    private void openBedrockImpl(Player player) {
        final SimpleForm.Builder builder = SimpleForm.builder()
                .title(title);

        for (PosMenuItem posItem : menuItems) {
            MenuItem item = posItem.menuItem();
            List<String> buttonText = new ArrayList<>();
            Component text = item.getIcon().getItemMeta().displayName();
            if (text == null) {
                text = item.getIcon().displayName();
            }
            if (item.isShiny()) {
                text = text.color(NamedTextColor.GREEN);
            }
            if (text instanceof TextComponent) buttonText.add(((TextComponent) text).content());
            else buttonText.add(item.getIcon().getI18NDisplayName());
            if (item.getIcon().lore() != null) {
                for (Component i : Objects.requireNonNull(item.getIcon().lore())) {
                    if (i instanceof TextComponent) {
                        buttonText.add(((TextComponent) i).content());
                    }
                }
            }
            if (item.isClose()) {
                builder.button(String.join("\n", buttonText));
            } else {
                builder.content(String.join("\n", buttonText));
            }
        }

        builder.responseHandler((form, data) -> {
            final SimpleFormResponse res = form.parseResponse(data);
            if (!res.isCorrect()) {
                return;
            }

            final List<MenuItem> item = menuItems.stream().map(PosMenuItem::menuItem).toList();
            final int id = Math.toIntExact(res.getClickedButtonId() + item.stream().filter(value -> !value.isClose()).count());
            final BiConsumer<MenuItem, Player> callback = item.get(id).getOnClick();
            if (callback != null) {
                callback.accept(item.get(id), player);
            }
        });

        final FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        fPlayer.sendForm(builder);
    }

    private void openJavaImpl(Player player) {
        inventory = Bukkit.createInventory(null, menuItems.stream().map(PosMenuItem::y).mapToInt(Integer::intValue).max().orElseThrow() * 9, Component.text(title));
        for (PosMenuItem menuItem : menuItems) {
            inventory.setItem(menuItem.x() - 1 + (menuItem.y() - 1) * 9, menuItem.menuItem().getIcon());
        }

        Bukkit.getPluginManager().registerEvents(this, Metaverseplugin.getInstance());
        player.openInventory(inventory);
    }

    /**
     * GUIを閉じたときにGCをするリスナー
     *
     * @param e イベント
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().equals(inventory)) {
            // GC
            HandlerList.unregisterAll(this);
        }
    }

    /**
     * GUIをクリックしたときにアイテムの処理をするリスナー
     *
     * @param e イベント
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(inventory)) {
            e.setCancelled(true);
            // Handle click
            for (PosMenuItem menuItem : menuItems) {
                if (e.getSlot() == menuItem.x() - 1 + (menuItem.y() - 1) * 9) {
                    if (menuItem.menuItem().getOnClick() != null) {
                        menuItem.menuItem().getOnClick().accept(menuItem.menuItem(), (Player) e.getWhoClicked());
                    }
                    if (menuItem.menuItem().isClose()) {
                        inventory.close();
                    }
                }
            }
        }
    }
}
