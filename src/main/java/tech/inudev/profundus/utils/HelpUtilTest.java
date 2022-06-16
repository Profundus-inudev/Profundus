package tech.inudev.profundus.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class HelpUtilTest {
    @Test
    public void test01() {
        String str = " ho ge ";
        testGetLines(str);
    }

    @Test
    public void test02() {
        String str = "  ho  ge  ";
        testGetLines(str);
    }

    @Test
    public void test03() {
        String str = " h o g e ";
        testGetLines(str);
    }

    @Test
    public void test04() {
        // error
        String str = "                                      ";
        testGetLines(str);
    }

    @Test
    public void test05() {
        String str = "LLLLLLLLLLLLLLLLLL   LLLLLLLLLLLLLLLL   LLLLLL";
        testGetLines(str);
    }


    @Test
    public void test06() {
        String str = "!\"#$%&'()=~|-^\\@[;:],./+*}`{<>?_";
        testGetLines(str);
    }

    @Test
    public void test07() {
        String str = "h$";
        testGetLines(str);
    }

    @Test
    public void test08() {
        String str = "$h";
        testGetLines(str);
    }

    @Test
    public void test09() {
        String str = " $$h@@ ";
        testGetLines(str);
    }

    @Test
    public void test10() {
        // error
        String str = "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh";
        testGetLines(str);
    }

    @Test
    public void test11() {
        // error
        String str = "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$";
        testGetLines(str);
    }

    @Test
    public void test12() {
        String str = "hhhhhhhhhhhhhhhhhh $$$$$$$$$$$$$";
        testGetLines(str);
    }

    @Test
    public void test13() {
        String str = "　あい　あい　";
        testGetLines(str);
    }

    @Test
    public void test14() {
        String str = "！”＃＄％＆’（）＝～｜￥＾－＠「」：；。、・￥‘｛＋＊｝＜＞？＿";
        testGetLines(str);
    }

    @Test
    public void test15() {
        // error?
        String str = "あああああああああああああ   ああああああああああああ";
        testGetLines(str);
    }

    @Test
    public void test16() {
        String str = """
                ああああああああああああ
                LLLLLLLLLLLLLLLLLLLLLLL
                $$$$$$$$$$$$$$$$$$$$$$$""";
        testGetLines(str);
    }

    @Test
    public void test17() {
        // error?
        String str = "§";
        testGetLines(str);
    }

    @Test
    public void test18() {
        // error?
        String str = "§§§";
        testGetLines(str);
    }

    @Test
    public void test19() {
        String str = " § § §§ ";
        testGetLines(str);
    }

    @Test
    public void test20() {
        // error,後ろにスペースがない
        String str = " §a§§b§l§L§k§r§§r aji";
        testGetLines(str);
    }

    @Test
    public void test21() {
        String str = "§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§";
        testGetLines(str);
    }

    @Test
    public void test22() {
        String str = "§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§a";
        testGetLines(str);
    }

    @Test
    public void test23() {
        String str = "a§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§ab";
        testGetLines(str);
    }

    @Test
    public void test24() {
        String str = "LLLLLLLLLLLLLLLLLLL\n" +
                "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh";
        testGetLines(str);
    }

    @Test
    public void test25() {
        String str = "あああああああああああああ   ああああああああああああ";
        testGetLines(str);
    }

    @Test
    public void test26() {
        String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        testGetLines(str);
    }

    @Test
    public void test27() {
        String str = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";
        testGetLines(str);
    }

    @Test
    public void test28() {
        HelpUtil.testHelpText(HelpUtil.HelpType.Test);
    }

    private void testGetLines(String str) {
        List<String> result = HelpUtil.getBookLines(Arrays.stream(str.split("\n")).toList());
        result.forEach(v -> {
            System.out.println(":" + v + ";");
            // 空白が表示されないな
        });
        System.out.println(";;;;;;;;;;;;;;");
        result.forEach(v -> {
            System.out.println(":" + HelpUtil.getRawNewWord(v) + ";");
            // 空白が表示されないな
        });
    }
}