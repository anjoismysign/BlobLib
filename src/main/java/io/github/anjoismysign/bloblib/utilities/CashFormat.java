package io.github.anjoismysign.bloblib.utilities;

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
            return TextColor.PARSE("&a+ $&l" + numberFormat.format(number).replace("\u00A0", ","));
        else
            return TextColor.PARSE("&c- $&l" + numberFormat.format(number).replace("\u00A0", ","));
    }
}
