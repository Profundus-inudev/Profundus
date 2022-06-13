package tech.inudev.profundus.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.map.MinecraftFont;
import tech.inudev.profundus.Profundus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ヘルプの表示するためのクラス
 *
 * @author toru-toruto
 */
public class HelpUtil {
    /**
     * それぞれのヘルプの情報を管理する列挙型
     */
    public enum HelpType {
        Test("test.txt", "Test"),
        Sample("sample.txt", "Sample");

        private final String fileName;
        private final String title;

        HelpType(String fileName, String title) {
            this.fileName = fileName;
            this.title = title;
        }
    }

    /**
     * ヘルプを開く
     *
     * @param playerUUID ヘルプを開く対象のプレイヤー
     * @param helpType   開くヘルプの種類
     */
    public static void openHelp(UUID playerUUID, HelpType helpType) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline()) {
            throw new IllegalArgumentException("オンラインでないか存在しないプレイヤーです。");
        }

        ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = ((BookMeta) writtenBook.getItemMeta())
                .author(Component.text("Master"))
                .title(Component.text(helpType.title));

        // テキストファイルからの読み込み
        InputStream stream = Profundus.getInstance().getResource("help/" + helpType.fileName);
        if (stream == null) {
            throw new IllegalArgumentException("ヘルプファイルが見つかりません。");
        }
        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        Stream<String> lines = new BufferedReader(reader).lines();
        String str = lines.collect(Collectors.joining("\n"));

        // 本に合わせて整形
        List<Component> pageList = new ArrayList<>();
        List<String> bookLines = HelpUtil.getLines(str);
        String page = "";
        for (int i = 0; i < bookLines.size(); i++) {
            page += bookLines.get(i) + "\n";
            if (i != 0 && (i % 13 == 0 || i == bookLines.size() - 1)) {
                pageList.add(Component.text(page));
                Profundus.getInstance().getLogger().info(page);
                page = "";
            }
        }

        bookMeta.addPages(pageList.toArray(new Component[0]));
        writtenBook.setItemMeta(bookMeta);

        player.openBook(writtenBook);
    }

    /**
     * 与えられたテキストを本のサイズに合わせて整形し、行のリストとして返す。
     * 英単語などの半角スペースなしで続く文字列が行に入りきらない場合、文字列をまとめて次行に折り返す。
     * カラーコードや装飾コードは、ボールドを除いて使用可能。（ボールドは文字の幅が変化するため現状は不可(2022/06/13)）
     *
     * @param text 本に表示するテキスト
     * @return 本のサイズに合わせて整形されたテキストの行ごとのリスト
     */
    private static List<String> getLines(String text) {

        List<String> resultLines = new ArrayList<>();

        // それぞれの行についてループ
        for (String paragraph : text.lines().toList()) {
            if (paragraph.equals("")) {
                // 空行の場合
                resultLines.add("");
            } else {
                resultLines.addAll(buildLines(paragraph));
            }
        }
        return resultLines;
    }

    private static List<String> buildLines(String paragraph) {
        final List<String> newLines = new ArrayList<>();

        final MinecraftFont font = new MinecraftFont();

        StringBuilder newLineBuilder = new StringBuilder();
        int newLineWidth = 0;

        // 半角スペースで分割した各文字列をループ（主に英単語をまとめて折り返す目的）
        for (String word : paragraph.split(" ")) {
            if (word.equals("")) {
                // 文頭や連続半角スペースの場合
                newLineBuilder.append(" ");
            } else {
                JoinWordResult result =
                        joinWord(newLineBuilder.toString(), newLineWidth, word, font);
                newLines.addAll(result.newLines);
                newLineBuilder = new StringBuilder(result.newLineStr);
                newLineWidth = result.newLineWidth;
            }
        }
        if (!newLineBuilder.toString().equals("")) {
            newLines.add(newLineBuilder.toString());
        }
//        resultLines.add(newLineBuilder);
        return newLines;
    }

    private static class JoinWordResult {
        List<String> newLines;
        String newLineStr;
        int newLineWidth;

        JoinWordResult(List<String> newLines, String newLineStr, int newLineWidth) {
            this.newLines = newLines;
            this.newLineStr = newLineStr;
            this.newLineWidth = newLineWidth;
        }
    }

    private static JoinWordResult joinWord(String lineStr, int newLineWidth, String word, MinecraftFont font) {
        List<String> newLines = new ArrayList<>();
        StringBuilder newLineStr = new StringBuilder(lineStr);

        // 文字列を文字単位に分割しループ
        String[] letters = word.split("");
        int id = 0;
        while (id < letters.length) {
            if (isSectionLetter(letters[id])) {
                DecorationResult result
                        = joinDecorationLetters(newLineStr.toString(), letters, id);
                newLineStr = new StringBuilder(result.newLineStr);
                id = result.id;
            } else {
                final int maxLineWidth = font.getWidth("LLLLLLLLLLLLLLLLLLL");
                final int letterMargin = 1;
                JoinLetterResult result = font.isValid(letters[id])
                        ? joinRegisteredLetters(newLineStr.toString(), letters, id, newLineWidth, font, maxLineWidth, letterMargin)
                        : joinUnregisteredLetter(newLineStr.toString(), letters, id, newLineWidth, maxLineWidth, letterMargin);
                if (result.newLines.size() > 0) {
                    newLines.addAll(result.newLines);
                }
                newLineStr = new StringBuilder(result.newLineStr);
                id = result.id;
                newLineWidth = result.newLineWidth;
            }
        }
        return new JoinWordResult(newLines, newLineStr.toString(), newLineWidth);
    }

    private static class DecorationResult {
        String newLineStr;
        int id;

        DecorationResult(String newLineStr, int id) {
            this.newLineStr = newLineStr;
            this.id = id;
        }
    }

    private static DecorationResult joinDecorationLetters(String lineStr, String[] letters, int id) {
        StringBuilder newLineStr = new StringBuilder(lineStr);
        if (id < letters.length - 1 && isDecorationLetter(letters[id + 1])) {
            if (!letters[id + 1].matches("[lL]")) {
                newLineStr.append(letters[id]).append(letters[id + 1]);
            }
            id += 2;
        } else {
            id++;
        }
        return new DecorationResult(newLineStr.toString(), id);
    }

    private static class JoinLetterResult {
        List<String> newLines;
        String newLineStr;
        int id;
        int newLineWidth;

        JoinLetterResult(List<String> newLines, String newLineStr, int id, int newLineWidth) {
            this.newLines = newLines;
            this.newLineStr = newLineStr;
            this.id = id;
            this.newLineWidth = newLineWidth;
        }
    }

    private static JoinLetterResult joinRegisteredLetters(
            String lineStr,
            String[] letters, int id, int newLineWidth,
            MinecraftFont font, int maxLineWidth, int letterMargin) {
        // MinecraftFontに文字が定義されている場合
        List<String> newLines = new ArrayList<>();
        StringBuilder newLineStr = new StringBuilder(lineStr);

        StringBuilder newWord = new StringBuilder(letters[id]);

        // 連続するMinecraftFontに定義される文字をまとめて処理
        while (id < letters.length - 1 && font.isValid(letters[id + 1])) {
            newWord.append(letters[id + 1]);
            id++;
        }
        // 追加される（主に）英単語がはみ出す場合、英単語ごと折り返す
        final int newWidth = (newLineStr.toString().equals("") ? 0 : letterMargin)
                + font.getWidth(newWord.toString());
        if (newLineWidth + newWidth > maxLineWidth) {
            // todo:ひと単語で一行埋めてしまう場合の特別処理が必要
//            resultLines.add(newLineStr.toString());
            newLines.add(newLineStr.toString());
            // 次の行へ
            newLineWidth = 0;
            newLineStr = new StringBuilder();
        }

        // 行へ追加
        newLineWidth += newLineStr.toString().equals("")
                ? font.getWidth(newWord.toString())
                : letterMargin + font.getWidth(newWord.toString());
        newLineStr.append(newWord);

        // 行のラストにスペースの余地があるならスペースを入れる
        final int endSpaceWidth = letterMargin + font.getWidth(" ");
        if (id == letters.length - 1 && newLineWidth + endSpaceWidth <= maxLineWidth) {
            newLineWidth += endSpaceWidth;
            newLineStr.append(" ");
        }
        id++;
        return new JoinLetterResult(newLines, newLineStr.toString(), id, newLineWidth);
    }

    private static JoinLetterResult joinUnregisteredLetter(
            String lineStr,
            String[] letters, int id, int newLineWidth,
            int maxLineWidth, int letterMargin) {
        // MinecraftFontに文字が定義されていない場合（日本語やその他の文字）
        final int letterWidth = 8; // 全角文字の幅基準
        List<String> newLines = new ArrayList<>();

        StringBuilder newLineStr = new StringBuilder(lineStr);

        // 追加される文字がはみ出す場合、折り返す
        final int newWidth = letterMargin + letterWidth;
        if (newLineWidth + newWidth > maxLineWidth) {
//            resultLines.add(newLineStr.toString());
            newLines.add(newLineStr.toString());
            // 次の行へ
            newLineWidth = 0;
            newLineStr = new StringBuilder();
        }

        // 行へ追加
        newLineWidth += (newLineStr.toString().equals(""))
                ? letterWidth
                : letterMargin + letterWidth;
        newLineStr.append(letters[id]);
        id++;
        return new JoinLetterResult(newLines, newLineStr.toString(), id, newLineWidth);
    }

    private static boolean isSectionLetter(String letter) {
        return letter.equals(String.valueOf(ChatColor.COLOR_CHAR));
    }

    private static boolean isDecorationLetter(String letter) {
        String regex = "(?i)[0-9A-FK-ORX]";
        return letter.matches(regex);
    }
}
