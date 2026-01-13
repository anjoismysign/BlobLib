package io.github.anjoismysign.bloblib.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StringUtil {

    public static String listStringCompactor(List<String> list) {
        if (list == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : list) {
            stringBuilder.append(s).append("%lsc:%");
        }
        return stringBuilder.toString();
    }

    public static String listStringCompactor(List<String> list, String key) {
        if (list == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : list) {
            stringBuilder.append(s).append(key);
        }
        return stringBuilder.toString();
    }

    public static List<String> listStringDecompactor(String compactedList) {
        ArrayList<String> list = new ArrayList<>();
        if (compactedList.equals("null"))
            return list;
        String[] strings = compactedList.split("%lsc:%");
        list.addAll(Arrays.asList(strings));
        return list;
    }

    public static List<String> listStringDecompactor(String compactedList, String key) {
        ArrayList<String> list = new ArrayList<>();
        if (compactedList.equals("null"))
            return list;
        String[] strings = compactedList.split(key);
        list.addAll(Arrays.asList(strings));
        return list;
    }

    public static List<String> replace(List<String> lore, Map<String, String> replacements) {
        List<String> result = new ArrayList<>();
        for (String s : lore) {
            for (String key : replacements.keySet()) {
                s = s.replace(key, replacements.get(key));
            }
            result.add(s);
        }
        return result;
    }
}
