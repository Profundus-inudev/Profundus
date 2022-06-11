package tech.inudev.profundus.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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

public class HelpUtil {
    public static void openHelp(UUID playerUUID, int helpId) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null && player.isOnline()) {
            ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
            if (helpId == 0) {
                BookMeta bookMeta = ((BookMeta) writtenBook.getItemMeta())
                        .author(Component.text("Master"))
                        .title(Component.text("Help"));

                // テキストファイルからの読み込み
                InputStream stream = Profundus.getInstance().getResource("help/test.txt");
                if (stream == null) {
                    player.sendMessage(Component.text("error: opening help failed."));
                    return;
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
            }
            player.openBook(writtenBook);
        }
    }

    private static void logging(String str) {
        Profundus.getInstance().getLogger().info(str);
    }

    private static List<String> getLines(String text) {
        final MinecraftFont font = new MinecraftFont();
        final int maxLineWidth = font.getWidth("LLLLLLLLLLLLLLLLLLL");
        final int charMargin = 1;

        // それぞれの行について処理
        List<String> lineList = new ArrayList<>();
        text.lines().forEach((String section) -> {
            if (section.equals("")) {
                // 空行の場合
                lineList.add("");
            } else {
                // 行を半角スペースごとに分割し、それぞれの文字列を処理（主に英単語をまとめて折り返す目的）
                final String rawSection = ChatColor.stripColor(section);
                StringBuilder lineStr = new StringBuilder(); // 行の文字列を保存
                int lineWidth = 0;
                for (String word : rawSection.split(" ")) {
                    if (word.equals("")) {
                        lineStr.append(" ");
                        continue;
                    }
                    // 文字列を文字単位に分割し、それぞれ処理
                    String[] charList = word.split("");
                    int i = 0;
                    while (i < charList.length) {
                        if (font.isValid(charList[i])) {
                            // MinecraftFontに文字が定義されている場合
                            StringBuilder mcFontWord = new StringBuilder(charList[i]);

                            // 連続するMinecraftFontに定義される文字をまとめて処理
                            while (i < word.length() - 1 && font.isValid(charList[i + 1])) {
                                mcFontWord.append(charList[i + 1]);
                                i++;
                            }

                            // 追加される（主に）英単語がはみ出す場合、英単語ごと折り返す
                            final int newWidth = (lineStr.toString().equals("") ? 0 : charMargin)
                                    + font.getWidth(mcFontWord.toString());
                            if (lineWidth + newWidth > maxLineWidth) {
                                // todo:ひと単語で一行埋めてしまう場合の特別処理が必要
                                lineList.add(lineStr.toString());
                                // 次の行へ
                                lineWidth = 0;
                                lineStr = new StringBuilder();
                            }

                            // 行へ追加
                            lineWidth += lineStr.toString().equals("")
                                    ? font.getWidth(mcFontWord.toString())
                                    : charMargin + font.getWidth(mcFontWord.toString());
                            lineStr.append(mcFontWord);

                            // 行のラストにスペースの余地があるならスペースを入れる
                            final int endSpaceWidth = charMargin + font.getWidth(" ");
                            if (i == word.length() - 1 && lineWidth + endSpaceWidth <= maxLineWidth) {
                                lineWidth += endSpaceWidth;
                                lineStr.append(" ");
                            }
                        } else {
                            // MinecraftFontに文字が定義されていない場合（日本語やその他の文字）
                            final int charWidth = 8; // 全角文字の幅基準

                            // 追加される文字がはみ出す場合、折り返す
                            final int newWidth = charMargin + charWidth;
                            if (lineWidth + newWidth > maxLineWidth) {
                                lineList.add(lineStr.toString());
                                // 次の行へ
                                lineWidth = 0;
                                lineStr = new StringBuilder();
                            }

                            // 行へ追加
                            lineWidth += (lineStr.toString().equals(""))
                                    ? charWidth
                                    : charMargin + charWidth;
                            lineStr.append(charList[i]);
                        }
                        i++;
                    }
                }
                lineList.add(lineStr.toString());
            }
        });
        return lineList;
    }
}
