package io.github.anjoismysign.bloblib.utilities;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class IntegerRange {
    private static IntegerRange instance;

    public static IntegerRange getInstance() {
        if (instance == null) {
            instance = new IntegerRange();
        }
        return instance;
    }

    @NotNull
    private Set<Integer> single(String single) {
        if (single.startsWith("-")) {
            int number;
            try {
                number = Integer.parseInt(single);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid range: " + single);
            }
            return Set.of(number);
        }
        String[] range = single.split("-");
        switch (range.length) {
            case 1 -> {
                return Set.of(Integer.parseInt(range[0]));
            }
            case 2 -> {
                Set<Integer> set = new HashSet<>();
                int start;
                int end;
                try {
                    start = Integer.parseInt(range[0]);
                    end = Integer.parseInt(range[1]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid range: " + single);
                }
                for (int i = start; i <= end; i++) {
                    set.add(i);
                }
                return set;
            }
            default -> {
                throw new IllegalArgumentException("Invalid range: " + single);
            }
        }

    }

    public Set<Integer> parse(String toParse) {
        String[] multi = toParse.split(",");
        if (multi.length == 1) {
            return single(toParse);
        } else {
            Set<Integer> set = new HashSet<>();
            for (String single : multi) {
                set.addAll(single(single));
            }
            return set;
        }
    }
}
