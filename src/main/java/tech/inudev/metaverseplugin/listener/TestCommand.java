package tech.inudev.metaverseplugin.listener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.inudev.metaverseplugin.Metaverseplugin;
import tech.inudev.metaverseplugin.define.Money;
import tech.inudev.metaverseplugin.utils.DatabaseUtil;

import java.util.UUID;

/**
 * ・目的
 * ・送金処理が正常に行えるかどうかを確認
 * ・異常が発生した場合ロールバック処理を行えるかどうかを確認
 * ・処理手順
 * ・送金元は、財布であるか、口座であるか
 * ・送金先は、財布であるか、口座であるか
 * ・送金元の財布or口座が作られていない場合、エラーとなるかどうか(constructor)
 * ・金額のadd, removeは正常に動作するか（計算が正しいか）
 * ・金額計算に変数totalを加えた影響の確認
 * ・~~add, removeに負の数を与えた場合エラーとなるかどうか~~
 * ・removeで金額が不足した場合、falseを返し、財布の場合はプレイヤーがオンラインのとき通知されるか
 * ・財布の場合でプレイヤーがオンラインのとき、または口座の場合、エラーを出さずにfalseを返すかどうか
 * ・送金先の財布or口座が作られていない場合、エラーとなるかどうか(push)
 * ・送金先のお金が不足している場合、
 * ・送金元プレイヤーがオンラインならば、"送金処理に失敗しました"
 * ・送金先プレイヤーがオンラインならば、"お金が足りません"
 * ・データベースの更新処理は正常に動作するか
 * ・データベースの更新処理が失敗した場合、エラーとなり、ロールバック処理が正常に行われるか
 * ・データベースの更新処理で正しい計算結果が保存されるか
 * ・データベースの更新処理が完了した場合、コミットされ、次回ロールバック処理を行っても、今回の処理が戻らないか
 */

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args) {
        if (!command.getName().equalsIgnoreCase("money")) {
            return false;
        }

        DatabaseUtil.recreateMoneyTable();

        UUID playerUUID = ((Player) sender).getUniqueId();
        String playerBank = "my bank";
        UUID partnerUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
        String partnerBank = "his bank";

        Money m0 = null;

        if (Integer.parseInt(args[0]) == 0) {
            if (Integer.parseInt(args[1]) != 0) {
                // 財布存在しない
                Money.createPlayerWallet(playerUUID);
            }
            m0 = new Money(playerUUID);
        } else if (Integer.parseInt(args[0]) == 1) {
            if (Integer.parseInt(args[1]) != 0) {
                // 口座存在しない
                Money.createBankAccount(playerBank);
            }
            m0 = new Money(playerBank);
        }

        if (m0 == null) {
            Metaverseplugin.getInstance().getLogger().info("無効なコマンド");
        }

        m0.add(100);
        m0.remove(10);
        if (Integer.parseInt(args[1]) == 1) {
            // remove お金不足
            m0.remove(200);
        }
        // ownAmount, total計算チェック（ownAmountは変化なし、totalで計算されるはず）
        logging("" + m0.getOwnAmount() + "," + m0.getTotal());

        Money m1 = null;
        if (Integer.parseInt(args[1]) == 2) {
            if (Integer.parseInt(args[2]) != 0) {
                // 送金先存在しない
                Money.createPlayerWallet(partnerUUID);
            }
            if (Integer.parseInt(args[2]) != 1) {
                // 送金先お金不足
                logging("お金補充");
                DatabaseUtil.updateMoneyAmount(partnerUUID.toString(), 1000);
            }

            m0.push(partnerUUID);
        } else if (Integer.parseInt(args[1]) == 3) {
            if (Integer.parseInt(args[2]) != 0) {
                // 送金先存在しない
                Money.createBankAccount(partnerBank);
            }
            if (Integer.parseInt(args[2]) != 1) {
                // 送金先お金不足
                logging("お金補充");
                DatabaseUtil.updateMoneyAmount(partnerBank, 1000);
            }

            m0.push(partnerBank);
        }

        // sqlite3で更新処理結果を確認

        return false;
    }

    private void logging(String msg) {
        Metaverseplugin.getInstance().getLogger().info(msg);
    }
}
