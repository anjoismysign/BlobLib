package us.mytheria.bloblib.utilities;

import java.text.DecimalFormat;

public class Formatter {

    public static String WATTS(float value) {
        String[] arr = {"", "k", "M", "G", "T", "P", "E", "Z", "Y"};
        int index = 0;
        while ((value / 1000) >= 1) {
            value = value / 1000;
            index++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s", decimalFormat.format(value), arr[index]) + "W";
    }

    public static String BYTES(float value) {
        String[] arr = {"", "Ki", "Mi", "Gi", "Ti", "Pi", "Ei", "Zi", "Yi"};
        int index = 0;
        while ((value / 1024) >= 1) {
            value = value / 1024;
            index++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s", decimalFormat.format(value), arr[index]) + "B";
    }

    public static String GRAMS(float value) {
        String[] arr = {"g", "kg", "t", "kt", "mt", "gt", "tt", "pt", "et", "zt"};
        int index = 0;
        while ((value / 1000) >= 1) {
            value = value / 1000;
            index++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s", decimalFormat.format(value), arr[index]);
    }
}
