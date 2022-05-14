package tech.inudev.metaverseplugin.utils;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class MenuItem {
    @Getter
    private final Consumer<MenuItem> onClick;
    @Getter
    private final ItemStack icon;
    @Getter
    private final Object customData;
    @Getter
    private final boolean shiny;
    @Getter
    private final Boolean close;
    @Getter
    private final int x;
    @Getter
    private final int y;

    /**
     * メニューのアイテム。
     *
     * @param onClick    クリック時のイベント
     * @param close      クリック時にGUIを閉じるか
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param shiny      ブロックをキラキラさせるか
     * @param x          アイテムの場所(x軸)。左上が0
     * @param y          アイテムの場所(y軸)。左上が0
     */
    public MenuItem(Consumer<MenuItem> onClick, ItemStack icon, Object customData, boolean shiny, boolean close, int x, int y) {
        this.onClick = onClick;
        this.icon = icon;
        this.customData = customData;
        this.shiny = shiny;
        this.close = close;
        this.x = x;
        this.y = y;

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
    public MenuItem(Consumer<MenuItem> onClick, ItemStack icon, Object customData, boolean shiny, int x, int y) {
        this(onClick, icon, customData, shiny, true, x, y);
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
    public MenuItem(Consumer<MenuItem> onClick, ItemStack icon, Object customData, int x, int y) {
        this(onClick, icon, customData, false, x, y);
    }

    /**
     * メニューのアイテム。
     *
     * @param onClick クリック時のイベント
     * @param icon    アイテムのブロック
     * @param x       アイテムの場所(x軸)。左上が0
     * @param y       アイテムの場所(y軸)。左上が0
     */
    public MenuItem(Consumer<MenuItem> onClick, ItemStack icon, int x, int y) {
        this(onClick, icon, null, x, y);
    }

    /**
     * メニューのアイテム。
     *
     * @param onClick クリック時のイベント
     * @param x       アイテムの場所(x軸)。左上が0
     * @param y       アイテムの場所(y軸)。左上が0
     */
    public MenuItem(Consumer<MenuItem> onClick, int x, int y) {
        this(onClick, null, x, y);
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
    public MenuItem(Consumer<MenuItem> onClick, ItemStack icon, boolean shiny, boolean close, int x, int y) {
        this(onClick, icon, null, shiny, close, x, y);
    }
}
