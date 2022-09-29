package us.mytheria.bloblib.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StringUtil {

    public static String listStringCompactor(List<String> list) {
        if (list == null) {
            return "null";
        }
        String listString = "";
        for (String s : list) {
            listString += s + "%lsc:%";
        }
        return listString;
    }

    public static String listStringCompactor(List<String> list, String key) {
        if (list == null) {
            return "null";
        }
        String listString = "";
        for (String s : list) {
            listString += s + key;
        }
        return listString;
    }

    public static List<String> listStringDecompactor(String compactedList) {
        ArrayList<String> list = new ArrayList<>();
        if (compactedList.equals("null"))
            return list;
        String[] strings = compactedList.split("%lsc:%");
        for (int i = 0; i < strings.length; i++) {
            list.add(strings[i]);
        }
        return list;
    }

    public static List<String> listStringDecompactor(String compactedList, String key) {
        ArrayList<String> list = new ArrayList<>();
        if (compactedList.equals("null"))
            return list;
        String[] strings = compactedList.split(key);
        for (int i = 0; i < strings.length; i++) {
            list.add(strings[i]);
        }
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
