package us.mytheria.bloblib.entities;

import net.md_5.bungee.api.ChatColor;
import us.mytheria.bloblib.entities.message.BlobMessage;

import java.util.Locale;
import java.util.function.Function;

/**
 * @param <T> The type of the message
 * @author anjoismysign
 * <p>
 * This class is meant to be used to modify a BlobMessage.
 * This is due since BlobMessage can hold multiple fields
 * that are meant to be a message. An example of these are
 * a simple BlobTitleMessage which holds Title and Subtitle
 * or a more complex such as BlobChatActionbarMessage which
 * holds a Chat message and an Actionbar message.
 */
public class BlobMessageModder<T extends BlobMessage> {
    private BlobMessage blobMessage;

    /**
     * Will create a new instance of BlobMessageModder.
     *
     * @param blobMessage The message to modify
     * @param <T>         The type of the message
     * @return The BlobMessageModder
     */
    public static <T extends BlobMessage> BlobMessageModder<T> mod(T blobMessage) {
        BlobMessageModder<T> messageModder = new BlobMessageModder<>();
        messageModder.blobMessage = blobMessage;
        return messageModder;
    }

    /**
     * @param function The function to modify the message with
     * @return The BlobMessageModder so it can be chained
     */
    public BlobMessageModder<T> modify(Function<String, String> function) {
        blobMessage = blobMessage.modify(function);
        return this;
    }

    /**
     * @param old         The string to replace
     * @param replacement The replacement
     * @return Replaces all occurrences with a replacement. CaSe SeNsItIvE
     */
    public BlobMessageModder<T> replace(String old, String replacement) {
        return modify(s -> s.replace(old, replacement));
    }

    /**
     * @return The message in lower case
     */
    public BlobMessageModder<T> lowerCase() {
        return modify(String::toLowerCase);
    }

    /**
     * @param locale The locale to use
     * @return The message in lower case
     */
    public BlobMessageModder<T> lowerCase(Locale locale) {
        return modify(s -> s.toLowerCase(locale));
    }

    /**
     * @return The message in upper case
     */
    public BlobMessageModder<T> upperCase() {
        return modify(String::toUpperCase);
    }

    /**
     * @param locale The locale to use
     * @return The message in upper case
     */
    public BlobMessageModder<T> upperCase(Locale locale) {
        return modify(s -> s.toUpperCase(locale));
    }

    /**
     * @return Makes sure that first letter is capitalized
     */
    public BlobMessageModder<T> capitalize() {
        return modify(s -> s.substring(0, 1).toUpperCase() + s.substring(1));
    }

    /**
     * Replaces all occurrences of a regex in the message
     *
     * @param regex       The regex to replace
     * @param replacement The replacement
     * @return the modified message
     */
    public BlobMessageModder<T> replaceAll(String regex, String replacement) {
        return modify(s -> s.replaceAll(regex, replacement));
    }

    /**
     * Replaces first occurrence of a regex in the message
     *
     * @param regex       The regex to replace
     * @param replacement The replacement
     * @return the modified message
     */
    public BlobMessageModder<T> replaceFirst(String regex, String replacement) {
        return modify(s -> s.replaceFirst(regex, replacement));
    }

    /**
     * Replaces last occurrence of a regex in the message
     *
     * @param regex       The regex to replace
     * @param replacement The replacement
     * @return the modified message
     */
    public BlobMessageModder<T> replaceLast(String regex, String replacement) {
        return modify(s -> s.replaceFirst(regex + "(?!.*" + regex + ")", replacement));
    }

    /**
     * Replaces all occurrences of a regex in the message, ignoring case
     *
     * @param regex       The regex to replace
     * @param replacement The replacement
     * @return the modified message
     */
    public BlobMessageModder<T> replaceAllIgnoreCase(String regex, String replacement) {
        return modify(s -> s.replaceAll("(?i)" + regex, replacement));
    }

    /**
     * Replaces the first occurrence of a regex in the message
     *
     * @param regex       The regex to replace
     * @param replacement The replacement
     * @return the modified message
     */
    public BlobMessageModder<T> replaceFirstIgnoreCase(String regex, String replacement) {
        return modify(s -> s.replaceFirst("(?i)" + regex, replacement));
    }

    /**
     * Replaces the last occurrence of a regex in the message
     *
     * @param regex       The regex to replace
     * @param replacement The replacement
     * @return the modified message
     */
    public BlobMessageModder<T> replaceLastIgnoreCase(String regex, String replacement) {
        return modify(s -> s.replaceFirst("(?i)" + regex + "(?!.*" + regex + ")", replacement));
    }

    /**
     * Strips the message of all color codes
     *
     * @return the modified message
     */
    public BlobMessageModder<T> stripColor() {
        return modify(ChatColor::stripColor);
    }

    /**
     * Translates existing message using an alternate color code character into a
     * String that uses the internal ChatColor.COLOR_CODE color code
     *
     * @param altColorChar The alternate color code character to replace. Ex: &amp;
     * @return the modified message
     */
    public BlobMessageModder<T> translateAlternateColorCodes(char altColorChar) {
        return modify(s -> ChatColor.translateAlternateColorCodes(altColorChar, s));
    }

    /**
     * Will color the message with the given color.
     *
     * @param color the color to use
     * @return the modified message
     */
    public BlobMessageModder<T> color(ChatColor color) {
        return modify(s -> color + s);
    }

    /**
     * Will trim the message, removing all leading and trailing whitespace.
     *
     * @return the modified message
     */
    public BlobMessageModder<T> trim() {
        return modify(String::trim);
    }

    /**
     * Will insert provided String at the end of the message,
     * the same way that append does.
     *
     * @param s the string to append
     * @return the modified message
     */
    public BlobMessageModder<T> concat(String s) {
        return append(s);
    }

    /**
     * Another way of concatenating. Will insert it at the end of the message.
     *
     * @param s the string to append
     * @return the modified message
     */
    public BlobMessageModder<T> append(String s) {
        return modify(s1 -> s1 + s);
    }

    /**
     * Prepends the given string to the message
     *
     * @param s the string to prepend
     * @return the modified message
     */
    public BlobMessageModder<T> prepend(String s) {
        return modify(s1 -> s + s1);
    }

    /**
     * Removes all occurrences of the given string. CaSe SeNsItIvE
     *
     * @param s the string to remove
     * @return the modified message
     */
    public BlobMessageModder<T> remove(String s) {
        return modify(s1 -> s1.replace(s, ""));
    }

    /**
     * Removes all occurrences of the given string, ignoring case
     *
     * @param s the string to remove
     * @return the modified message
     */
    public BlobMessageModder<T> removeIgnoreCase(String s) {
        return modify(s1 -> s1.replaceAll("(?i)" + s, ""));
    }

    /**
     * Removes the first occurrence of the given string. CaSe SeNsItIvE
     *
     * @param s the string to remove
     * @return the modified message
     */
    public BlobMessageModder<T> removeFirst(String s) {
        return modify(s1 -> s1.replaceFirst(s, ""));
    }

    /**
     * Removes the last occurrence of the given string. CaSe SeNsItIvE
     *
     * @param s the string to remove
     * @return the modified message
     */
    public BlobMessageModder<T> removeLast(String s) {
        return modify(s1 -> s1.replaceFirst(s + "(?!.*" + s + ")", ""));
    }

    /**
     * Removes the first occurrence of the given string, ignoring case
     *
     * @param s the string to remove
     * @return the modified message
     */
    public BlobMessageModder<T> removeFirstIgnoreCase(String s) {
        return modify(s1 -> s1.replaceFirst("(?i)" + s, ""));
    }

    /**
     * Removes the last occurrence of the given string, ignoring case
     *
     * @param s the string to remove
     * @return the modified message
     */
    public BlobMessageModder<T> removeLastIgnoreCase(String s) {
        return modify(s1 -> s1.replaceFirst("(?i)" + s + "(?!.*" + s + ")", ""));
    }

    /**
     * Removes all vowels from the message
     *
     * @return the modified message
     */
    public BlobMessageModder<T> removeVowels() {
        return modify(s -> s.replaceAll("[aeiouAEIOU]", ""));
    }

    /**
     * @return the modified message
     */
    @SuppressWarnings("unchecked")
    public T get() {
        return (T) blobMessage;
    }
}
