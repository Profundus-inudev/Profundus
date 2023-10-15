package tech.inudev.profundus.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tech.inudev.profundus.database.User;

import java.util.Objects;

/**
 * Login関係listener実装クラス
 *
 * @author kidocchy
 */
public class LoginEvent implements Listener {
    /**
     * PlayerJoin時呼び出し
     * Userの最終ログインをアップデート
     *
     * @param e サーバーから渡される。
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Objects.requireNonNull(User.getByPlayer(e.getPlayer(), true)).updateLastLogin();

    }

}
