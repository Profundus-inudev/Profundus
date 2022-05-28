package tech.inudev.metaverseplugin.define;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

/**
 * GUIを構成するアイテムを表すクラス
 *
 * @author kumitatepazuru
 */
public class MenuItem {
    @Getter
    private final BiConsumer<MenuItem, Player> onClick;
    @Getter
    private final ItemStack icon;
    @Getter
    private final Object customData;
    @Getter
    private final boolean shiny;
    @Getter
    private final boolean close;
    @Getter
    private final String title;

    /**
     * メニューのアイテム。
     *
     * @param title      アイテムのタイトル
     * @param onClick    クリック時のイベント
     * @param close      クリック時にGUIを閉じるか
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param shiny      ブロックをキラキラさせるか
     */
    public MenuItem(String title, BiConsumer<MenuItem, Player> onClick, ItemStack icon, Object customData, boolean shiny, boolean close) {
        this.onClick = onClick;
        this.icon = icon;
        this.customData = customData;
        this.shiny = shiny;
        this.close = close;
        this.title = title;

        if (shiny) {
            icon.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 1);
        }
    }

    /**
     * メニューのアイテム。
     *
     * @param title      アイテムのタイトル
     * @param onClick    クリック時のイベント
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param shiny      ブロックをキラキラさせるか
     */
    public MenuItem(String title, BiConsumer<MenuItem, Player> onClick, ItemStack icon, Object customData, boolean shiny) {
        this(title, onClick, icon, customData, shiny, true);
    }

    /**
     * メニューのアイテム。
     *
     * @param title      アイテムのタイトル
     * @param onClick    クリック時のイベント
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     */
    public MenuItem(String title, BiConsumer<MenuItem, Player> onClick, ItemStack icon, Object customData) {
        this(title, onClick, icon, customData, false);
    }

    /**
     * メニューのアイテム。
     *
     * @param title   アイテムのタイトル
     * @param onClick クリック時のイベント
     * @param icon    アイテムのブロック
     */
    public MenuItem(String title, BiConsumer<MenuItem, Player> onClick, ItemStack icon) {
        this(title, onClick, icon, null);
    }

    /**
     * メニューのアイテム。
     *
     * @param title   アイテムのタイトル
     * @param onClick クリック時のイベント
     */
    public MenuItem(String title, BiConsumer<MenuItem, Player> onClick) {
        this(title, onClick, new ItemStack(Material.STONE));
    }

    /**
     * メニューのアイテム。
     *
     * @param title   アイテムのタイトル
     * @param onClick クリック時のイベント
     * @param icon    アイテムのブロック
     * @param shiny   ブロックをキラキラさせるか
     * @param close   クリック時にGUIを閉じるか
     */
    public MenuItem(String title, BiConsumer<MenuItem, Player> onClick, ItemStack icon, boolean shiny, boolean close) {
        this(title, onClick, icon, null, shiny, close);
    }

    /**
     * メニューのアイテム。
     *
     * @param title アイテムのタイトル
     */
    public MenuItem(String title) {
        this(title, null, new ItemStack(Material.STONE), null);
    }
}
