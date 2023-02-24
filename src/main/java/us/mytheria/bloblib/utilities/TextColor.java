package us.mytheria.bloblib.utilities;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextColor {
    private final char COLOR_CHAR = ChatColor.COLOR_CHAR;

    private String translateHexColorCodes(String message, char colorCodeCharacter) {
        //Sourced from this post by imDaniX: https://github.com/SpigotMC/BungeeCord/pull/2883#issuecomment-653955600
        Matcher matcher = Pattern.compile(colorCodeCharacter + "(#[A-Fa-f0-9]{6})").matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
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
        return ChatColor.translateAlternateColorCodes(alternateColorChar, textColor.translateHexColorCodes(textToTranslate, alternateColorChar));
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
        return ChatColor.translateAlternateColorCodes('&', textToTranslate);
    }
}
