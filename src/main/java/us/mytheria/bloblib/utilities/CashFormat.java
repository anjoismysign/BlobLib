package us.mytheria.bloblib.utilities;

import net.md_5.bungee.api.ChatColor;

import java.text.NumberFormat;

public class CashFormat {

    static NumberFormat numberFormat;

    static {
        numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);
        numberFormat.setMaximumFractionDigits(2);
    }

    public static String numberFormat(Double number) {
        return numberFormat.format(number).replace("\u00A0", ",");
    }

    public static String format(double number) {
        if (number >= 0)
            return ChatColor.translateAlternateColorCodes('&',
                    "&a+ $&l" + numberFormat.format(number).replace("\u00A0", ","));
        else
            return ChatColor.translateAlternateColorCodes('&',
                    "&c- $&l" + numberFormat.format(number).replace("\u00A0", ","));
    }
}
