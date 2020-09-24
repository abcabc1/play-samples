package utils;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import me.xdrop.fuzzywuzzy.FuzzySearch;

public class StringUtil {

    public static String convertToPinyinString(String text, String separator, PinyinFormat format) {
        try {
            return PinyinHelper.convertToPinyinString(text, separator, format);
        } catch (PinyinException e) {
            return "";
        }
    }

    public static String rounding(int i, int spaceNum) {
        return String.format("%0" + spaceNum + "d", i);//%03d
    }

    public static int ratio(String s1, String s2) {
        return FuzzySearch.ratio(s1, s2);
    }

    public static int weightedRatio(String s1, String s2) {
        return FuzzySearch.weightedRatio(s1, s2);
    }

    public static boolean isAlpha(char c) {
        return (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
    }

    public static boolean isNumber(char c) {
        return c >= 48 && c <= 57;
    }

    public static boolean isChineseByScript(char c) {
        Character.UnicodeScript sc = Character.UnicodeScript.of(c);
        return  sc == Character.UnicodeScript.HAN;
    }

    public static int indexOfChinese(String str) {
        int index = -1;
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChineseByScript(c)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static int indexOfAlpha(String str) {
        int index = -1;
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isAlpha(c)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static int indexOfNumber(String str) {
        int index = -1;
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isNumber(c)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static boolean hasChinese(String str) {
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChineseByScript(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isChinese(String str) {
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!isChineseByScript(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumber(String str) {
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!isNumber(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlpha(String str) {
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!isAlpha(c)) {
                return false;
            }
        }
        return true;
    }
}
