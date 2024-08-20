package us.mytheria.bloblib.entities.translatable;

import net.md_5.bungee.api.ChatColor;
import us.mytheria.bloblib.utilities.TextColor;

import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @param <T, R> The type of the translatable
 * @author anjoismysign
 * <p>
 * This class is meant to be used to modify a BlobTranslatable.
 * This is due since BlobTranslatable can hold multiple fields
 * that are meant to be a translatable.
 */
public class BlobTranslatableModder<T extends Translatable<R>, R> {
    protected T translatable;

    /**
     * @param function The function to modify the translatable with
     * @return The BlobTranslatableModder so it can be chained
     */
    @SuppressWarnings("unchecked")
    public BlobTranslatableModder<T, R> modify(Function<String, String> function) {
        this.translatable = (T) translatable.modify(function);
        return this;
    }

    /**
     * @param old         The string to replace
     * @param replacement The replacement
     * @return Replaces all occurrences with a replacement. CaSe SeNsItIvE
     */
    public BlobTranslatableModder<T, R> replace(String old, String replacement) {
        return modify(s -> s.replace(old, replacement));
    }

    private BlobTranslatableModder<T, R> regexReplace(String match, Function<String, String> function) {
        Pattern pattern = Pattern.compile(match);
        return modify(s -> {
            Matcher matcher = pattern.matcher(s);
            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                matcher.appendReplacement(sb, function.apply(matcher.group(1)));
            }
            matcher.appendTail(sb);
            return sb.toString();
        });
    }

    /**
     * Will match all occurrences of the given regex and replace them with the
     * result of the function by using a wildcard.
     * <p>
     * Example:
     * matchReplace("%flag@%", "@", s -> s); //Will set all flag placeholders as
     * //whatever the flag is.
     * <p>
     *
     * @param match    The regex to match
     * @param wildcard The wildcard to use
     * @param function The function to use
     * @return The modified translatable
     */
    public BlobTranslatableModder<T, R> matchReplace(String match, String wildcard, Function<String, String> function) {
        String regex = match.replace(wildcard, "(.*?)");
        return regexReplace(regex, function);
    }

    /**
     * Will match all occurrences of the given regex and replace them with the
     * result of the function.
     * Will use '@' as the wildcard.
     *
     * @param match    The regex to match
     * @param function The function to use
     * @return The modified translatable
     */
    public BlobTranslatableModder<T, R> matchReplace(String match, Function<String, String> function) {
        return matchReplace(match, "@", function);
    }

    /**
     * @return The translatable in lower case
     */
    public BlobTranslatableModder<T, R> lowerCase() {
        return modify(s -> s.toLowerCase(Locale.ROOT));
    }

    /**
     * @param locale The locale to use
     * @return The translatable in lower case
     */
    public BlobTranslatableModder<T, R> lowerCase(Locale locale) {
        return modify(s -> s.toLowerCase(locale));
    }

    /**
     * @return The translatable in upper case
     */
    public BlobTranslatableModder<T, R> upperCase() {
        return modify(s -> s.toUpperCase(Locale.ROOT));
    }

    /**
     * @param locale The locale to use
     * @return The translatable in upper case
     */
    public BlobTranslatableModder<T, R> upperCase(Locale locale) {
        return modify(s -> s.toUpperCase(locale));
    }

    /**
     * @return Makes sure that first letter is capitalized
     */
    public BlobTranslatableModder<T, R> capitalize() {
        return modify(s -> s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1));
    }

    /**
     * Replaces all occurrences of a regex in the translatable
     *
     * @param regex       The regex to replace
     * @param replacement The replacement
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> replaceAll(String regex, String replacement) {
        return modify(s -> s.replaceAll(regex, replacement));
    }

    /**
     * Replaces first occurrence of a regex in the translatable
     *
     * @param regex       The regex to replace
     * @param replacement The replacement
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> replaceFirst(String regex, String replacement) {
        return modify(s -> s.replaceFirst(regex, replacement));
    }

    /**
     * Replaces last occurrence of a regex in the translatable
     *
     * @param regex       The regex to replace
     * @param replacement The replacement
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> replaceLast(String regex, String replacement) {
        return modify(s -> s.replaceFirst(regex + "(?!.*" + regex + ")", replacement));
    }

    /**
     * Replaces all occurrences of a regex in the translatable, ignoring case
     *
     * @param regex       The regex to replace
     * @param replacement The replacement
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> replaceAllIgnoreCase(String regex, String replacement) {
        return modify(s -> s.replaceAll("(?i)" + regex, replacement));
    }

    /**
     * Replaces the first occurrence of a regex in the translatable
     *
     * @param regex       The regex to replace
     * @param replacement The replacement
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> replaceFirstIgnoreCase(String regex, String replacement) {
        return modify(s -> s.replaceFirst("(?i)" + regex, replacement));
    }

    /**
     * Replaces the last occurrence of a regex in the translatable
     *
     * @param regex       The regex to replace
     * @param replacement The replacement
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> replaceLastIgnoreCase(String regex, String replacement) {
        return modify(s -> s.replaceFirst("(?i)" + regex + "(?!.*" + regex + ")", replacement));
    }

    /**
     * Strips the translatable of all color codes
     *
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> stripColor() {
        return modify(ChatColor::stripColor);
    }

    /**
     * Translates existing translatable using an alternate color code character into a
     * String that uses the internal ChatColor.COLOR_CODE color code
     *
     * @param altColorChar The alternate color code character to replace. Ex: &amp;
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> translateRGBAndChatColors(char altColorChar) {
        return modify(s -> TextColor.CUSTOM_PARSE(altColorChar, s));
    }

    /**
     * Will color the translatable with the given color.
     *
     * @param color the color to use
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> color(ChatColor color) {
        return modify(s -> color + s);
    }

    /**
     * Will trim the translatable, removing all leading and trailing whitespace.
     *
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> trim() {
        return modify(String::trim);
    }

    /**
     * Will insert provided String at the end of the translatable,
     * the same way that append does.
     *
     * @param s the string to append
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> concat(String s) {
        return append(s);
    }

    /**
     * Another way of concatenating. Will insert it at the end of the translatable.
     *
     * @param s the string to append
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> append(String s) {
        return modify(s1 -> s1 + s);
    }

    /**
     * Prepends the given string to the translatable
     *
     * @param s the string to prepend
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> prepend(String s) {
        return modify(s1 -> s + s1);
    }

    /**
     * Removes all occurrences of the given string. CaSe SeNsItIvE
     *
     * @param s the string to remove
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> remove(String s) {
        return modify(s1 -> s1.replace(s, ""));
    }

    /**
     * Removes all occurrences of the given string, ignoring case
     *
     * @param s the string to remove
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> removeIgnoreCase(String s) {
        return modify(s1 -> s1.replaceAll("(?i)" + s, ""));
    }

    /**
     * Removes the first occurrence of the given string. CaSe SeNsItIvE
     *
     * @param s the string to remove
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> removeFirst(String s) {
        return modify(s1 -> s1.replaceFirst(s, ""));
    }

    /**
     * Removes the last occurrence of the given string. CaSe SeNsItIvE
     *
     * @param s the string to remove
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> removeLast(String s) {
        return modify(s1 -> s1.replaceFirst(s + "(?!.*" + s + ")", ""));
    }

    /**
     * Removes the first occurrence of the given string, ignoring case
     *
     * @param s the string to remove
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> removeFirstIgnoreCase(String s) {
        return modify(s1 -> s1.replaceFirst("(?i)" + s, ""));
    }

    /**
     * Removes the last occurrence of the given string, ignoring case
     *
     * @param s the string to remove
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> removeLastIgnoreCase(String s) {
        return modify(s1 -> s1.replaceFirst("(?i)" + s + "(?!.*" + s + ")", ""));
    }

    /**
     * Removes all vowels from the translatable
     *
     * @return the modified translatable
     */
    public BlobTranslatableModder<T, R> removeVowels() {
        return modify(s -> s.replaceAll("[aeiouAEIOU]", ""));
    }

    /**
     * @return the modified translatable
     */
    @SuppressWarnings("unchecked")
    public T get() {
        return translatable;
    }
}
