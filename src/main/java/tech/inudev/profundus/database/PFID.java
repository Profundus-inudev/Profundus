package tech.inudev.profundus.database;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import tech.inudev.profundus.Profundus;
import tech.inudev.profundus.database.DatabaseUtil.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

/**
 * プラグインで管理するIDを実装するクラス
 * 細かい実装はサブクラスで。
 *
 * @author kidocchy
 */
public abstract class PFID {

    /**
     * PFID上位ビット用NamespacedKey
     */
    public static final NamespacedKey msbPFID = new NamespacedKey(Profundus.getInstance(), "pfid/msb");
    /**
     * PFID下位ビット用NamespacedKey
     */
    public static final NamespacedKey lsbPFID = new NamespacedKey(Profundus.getInstance(), "pfid/lsb");
    @Getter
    UUID pfid;
    Table type;
    Instant createdAt;

    PFID(Table t) {
        pfid = newPFID(t);
        type = t;
        createdAt = Instant.now();
    }

    PFID() {
    }

    /**
     * PFIDをランダム発行。
     *
     * @param type Table
     * @return PFID
     */
    static UUID newPFID(Table type) {
        //とりあえず，ランダムで発行。
        UUID yourID = UUID.randomUUID();
        DBUPFID.insert(yourID, type);
        return yourID;
    }

    /**
     * PFIDを与えると，Table Enum(USER/ITEM/GROUPなど)を返却。
     *
     * @param pfid
     * @return DatabaseUtil.Table Enum
     */
    static Table getType(UUID pfid) {
        ResultSet rs = DatabaseUtil.selectUUID(Table.PFID, "PFID", pfid);
        try {
            Objects.requireNonNull(rs).first();
            return Table.valueOf(rs.getString("type"));
        } catch (SQLException e) {
            Profundus.getInstance().getLogger().log(Level.WARNING, e.toString());
        }
        return null;
    }

    /**
     * PFIDでエントリーを検索。
     * どの型が戻るかわからないので，ジェネリクスとした。
     *
     * @param <T>  PFIDのサブクラスのどれか。
     * @param pfid PFID
     * @return USERまたはPFGROUPの返却を実装。
     */
    @SuppressWarnings("unchecked")
    public static <T extends PFID> T getByPFID(UUID pfid) {
        if (Objects.requireNonNull(getType(pfid)) == Table.USER) {
            return (T) User.getByPFID(pfid);
        }
        return null;
    }

    /**
     * データベース追加用
     */
    protected abstract void addToDB();

    /**
     * データベース更新用
     */
    protected abstract void updateDB();

    /**
     * データベース削除用
     */
    protected abstract void removeFromDB();

}
