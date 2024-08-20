package us.mytheria.bloblib.utilities;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class Formatter {
    private static Formatter instance;

    private Formatter() {
    }

    public static Formatter getInstance() {
        if (instance == null)
            instance = new Formatter();
        return instance;
    }

    public String formatAll(
            @NotNull String input,
            double amount) {
        return input.replace(
                "%wattsBalance%", watts((float) amount)).replace(
                "%bytesBalance%", bytes((float) amount)).replace(
                "%gramsBalance%", grams((float) amount)).replace(
                "%litersBalance%", liters((float) amount)).replace(
                "%psi%", psi((float) amount));
    }

    private String watts(float value) {
        String[] arr = {"", "k", "M", "G", "T", "P", "E", "Z", "Y"};
        int index = 0;
        while ((value / 1000) >= 1) {
            value = value / 1000;
            index++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s", decimalFormat.format(value), arr[index]) + "W";
    }

    private String bytes(float value) {
        String[] arr = {"", "Ki", "Mi", "Gi", "Ti", "Pi", "Ei", "Zi", "Yi"};
        int index = 0;
        while ((value / 1024) >= 1) {
            value = value / 1024;
            index++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s", decimalFormat.format(value), arr[index]) + "B";
    }

    private String grams(float value) {
        String[] arr = {"g", "kg", "t", "kt", "Mt", "Gt", "Tt", "Pt", "Et", "Zt"};
        int index = 0;
        while ((value / 1000) >= 1) {
            value = value / 1000;
            index++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s", decimalFormat.format(value), arr[index]);
    }

    private String liters(float value) {
        String[] arr = {"L", "kL", "ML", "GL", "TL", "PL", "EL", "ZL", "YL"};
        int index = 0;
        while ((value / 1000) >= 1) {
            value = value / 1000;
            index++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s", decimalFormat.format(value), arr[index]);
    }

    private String psi(float value) {
        String[] arr = {"", "k", "M", "G", "T", "P", "E", "Z", "Y"};
        int index = 0;
        while ((value / 1000) >= 1) {
            value = value / 1000;
            index++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s", decimalFormat.format(value), arr[index]) + "psi";
    }
}
