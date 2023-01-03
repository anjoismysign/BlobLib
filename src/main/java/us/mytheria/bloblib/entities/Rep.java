package us.mytheria.bloblib.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Rep {
    private String check;
    private String replace;

    public static Rep lace(String check, String replace) {
        return new Rep(check, replace);
    }

    private Rep(String check, String replace) {
        this.check = check;
        this.replace = replace;
    }

    public String getCheck() {
        return check;
    }

    public String getReplace() {
        return replace;
    }

    public static List<String> lace(List<String> lore, Rep... reps) {
        if (lore == null)
            return null;
        List<String> newLore = new ArrayList<>();
        for (String string : lore) {
            for (Rep rep : reps) {
                string = string.replace(rep.getCheck(), rep.getReplace());
            }
            newLore.add(string);
        }
        return newLore;
    }

    public static List<String> lace(List<String> lore, Collection<Rep> reps) {
        if (lore == null)
            return null;
        List<String> newLore = new ArrayList<>();
        for (String string : lore) {
            for (Rep rep : reps) {
                string = string.replace(rep.getCheck(), rep.getReplace());
            }
            newLore.add(string);
        }
        return newLore;
    }
}
