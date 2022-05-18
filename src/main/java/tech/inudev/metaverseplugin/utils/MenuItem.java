package tech.inudev.metaverseplugin.utils;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

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
    private final int x;
    @Getter
    private final int y;
    @Getter private final String title;

    /**
     * メニューのアイテム。
     *
     * @param title      アイテムのタイトル
     * @param onClick    クリック時のイベント
     * @param close      クリック時にGUIを閉じるか
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param shiny      ブロックをキラキラさせるか
     * @param x          アイテムの場所(x軸)。左上が0
     * @param y          アイテムの場所(y軸)。左上が0
     */
    public MenuItem(String title, BiConsumer<MenuItem, Player> onClick, ItemStack icon, Object customData, boolean shiny, boolean close, int x, int y) {
        this.onClick = onClick;
        this.icon = icon;
        this.customData = customData;
        this.shiny = shiny;
        this.close = close;
        this.x = x;
        this.y = y;
        this.title = title;

        if(shiny) {
            icon.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 1);
        }
    }

    /**
     * メニューのアイテム。
     *
     * @param onClick    クリック時のイベント
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param shiny      ブロックをキラキラさせるか
     * @param x          アイテムの場所(x軸)。左上が0
     * @param y          アイテムの場所(y軸)。左上が0
     */
    public MenuItem(String title, BiConsumer<MenuItem, Player> onClick, ItemStack icon, Object customData, boolean shiny, int x, int y) {
        this(title,onClick, icon, customData, shiny, true, x, y);
    }

    /**
     * メニューのアイテム。
     *
     * @param onClick    クリック時のイベント
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param x          アイテムの場所(x軸)。左上が0
     * @param y          アイテムの場所(y軸)。左上が0
     */
    public MenuItem(String title, BiConsumer<MenuItem, Player> onClick, ItemStack icon, Object customData, int x, int y) {
        this(title,onClick, icon, customData, false, x, y);
    }

    /**
     * メニューのアイテム。
     *
     * @param onClick クリック時のイベント
     * @param icon    アイテムのブロック
     * @param x       アイテムの場所(x軸)。左上が0
     * @param y       アイテムの場所(y軸)。左上が0
     */
    public MenuItem(String title, BiConsumer<MenuItem, Player> onClick, ItemStack icon, int x, int y) {
        this(title,onClick, icon, null, x, y);
    }

    /**
     * メニューのアイテム。
     *
     * @param onClick クリック時のイベント
     * @param x       アイテムの場所(x軸)。左上が0
     * @param y       アイテムの場所(y軸)。左上が0
     */
    public MenuItem(String title, BiConsumer<MenuItem, Player> onClick, int x, int y) {
        this(title,onClick, new ItemStack(Material.STONE), x, y);
    }

    /**
     * メニューのアイテム。
     *
     * @param onClick クリック時のイベント
     * @param icon    アイテムのブロック
     * @param shiny   ブロックをキラキラさせるか
     * @param close   クリック時にGUIを閉じるか
     * @param x       アイテムの場所(x軸)。左上が0
     * @param y       アイテムの場所(y軸)。左上が0
     */
    public MenuItem(String title, BiConsumer<MenuItem, Player> onClick, ItemStack icon, boolean shiny, boolean close, int x, int y) {
        this(title,onClick, icon, null, shiny, close, x, y);
    }

    /**
     * メニューのアイテム。
     *
     * @param title アイテムのタイトル
     */
    public MenuItem(String title, int x, int y) {
        this(title, null, new ItemStack(Material.STONE), null, x, y);
    }
}
