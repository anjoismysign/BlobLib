package us.mytheria.bloblib.utilities;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextColor {

    public String translateHexColorCodes(char alternateColorCode, String message) {
        final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            char COLOR_CHAR = ChatColor.COLOR_CHAR;
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }

    /**
     * This method will translate alternate color codes into the proper color codes.
     * This method will also translate hex color codes into the proper color codes.
     *
     * @param alternateColorChar The alternate color code character to replace. Ex: '&'
     * @param textToTranslate    The text to translate.
     * @return The translated text.
     */
    public static String CUSTOM_PARSE(char alternateColorChar, String textToTranslate) {
        TextColor textColor = new TextColor();
        return ChatColor.translateAlternateColorCodes(alternateColorChar, textColor.translateHexColorCodes(alternateColorChar, textToTranslate));
    }

    /**
     * This method will translate alternate color codes into the proper color codes
     * by using  '&' as the alternate color code character.
     * This method will also translate hex color codes into the proper color codes.
     *
     * @param textToTranslate The text to translate.
     * @return The translated text.
     */
    public static String PARSE(String textToTranslate) {
        return CUSTOM_PARSE('&', textToTranslate);
    }
}
