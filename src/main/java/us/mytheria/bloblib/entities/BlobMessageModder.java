package us.mytheria.bloblib.entities;

import net.md_5.bungee.api.ChatColor;
import us.mytheria.bloblib.entities.message.BlobMessage;

import java.util.Locale;
import java.util.function.Function;

public class BlobMessageModder<T extends BlobMessage> {
    private BlobMessage blobMessage;

    public static <T extends BlobMessage> BlobMessageModder<T> mod(T blobMessage) {
        BlobMessageModder messageModder = new BlobMessageModder();
        messageModder.blobMessage = blobMessage;
        return messageModder;
    }

    public BlobMessageModder<T> modify(Function<String, String> function) {
        blobMessage = blobMessage.modify(function);
        return this;
    }

    public BlobMessageModder<T> replace(String old, String replacement) {
        return modify(s -> s.replace(old, replacement));
    }

    public BlobMessageModder<T> lowerCase() {
        return modify(String::toLowerCase);
    }

    public BlobMessageModder<T> lowerCase(Locale locale) {
        return modify(s -> s.toLowerCase(locale));
    }

    public BlobMessageModder<T> upperCase() {
        return modify(String::toUpperCase);
    }

    public BlobMessageModder<T> upperCase(Locale locale) {
        return modify(s -> s.toUpperCase(locale));
    }

    public BlobMessageModder<T> capitalize() {
        return modify(s -> s.substring(0, 1).toUpperCase() + s.substring(1));
    }

    public BlobMessageModder<T> replaceAll(String regex, String replacement) {
        return modify(s -> s.replaceAll(regex, replacement));
    }

    public BlobMessageModder<T> replaceFirst(String regex, String replacement) {
        return modify(s -> s.replaceFirst(regex, replacement));
    }

    public BlobMessageModder<T> replaceLast(String regex, String replacement) {
        return modify(s -> s.replaceFirst(regex + "(?!.*" + regex + ")", replacement));
    }

    public BlobMessageModder<T> replaceAllIgnoreCase(String regex, String replacement) {
        return modify(s -> s.replaceAll("(?i)" + regex, replacement));
    }

    public BlobMessageModder<T> replaceFirstIgnoreCase(String regex, String replacement) {
        return modify(s -> s.replaceFirst("(?i)" + regex, replacement));
    }

    public BlobMessageModder<T> replaceLastIgnoreCase(String regex, String replacement) {
        return modify(s -> s.replaceFirst("(?i)" + regex + "(?!.*" + regex + ")", replacement));
    }

    public BlobMessageModder<T> stripColor() {
        return modify(ChatColor::stripColor);
    }

    public BlobMessageModder<T> translateAlternateColorCodes(char altColorChar) {
        return modify(s -> ChatColor.translateAlternateColorCodes(altColorChar, s));
    }

    public BlobMessageModder<T> color(ChatColor color) {
        return modify(s -> color + s);
    }

    public BlobMessageModder<T> trim() {
        return modify(String::trim);
    }

    public BlobMessageModder<T> concat(String s) {
        return modify(s1 -> s1 + s);
    }

    public BlobMessageModder<T> prepend(String s) {
        return modify(s1 -> s + s1);
    }

    public BlobMessageModder<T> remove(String s) {
        return modify(s1 -> s1.replace(s, ""));
    }

    public BlobMessageModder<T> removeFirst(String s) {
        return modify(s1 -> s1.replaceFirst(s, ""));
    }

    public BlobMessageModder<T> removeLast(String s) {
        return modify(s1 -> s1.replaceFirst(s + "(?!.*" + s + ")", ""));
    }

    public BlobMessageModder<T> removeFirstIgnoreCase(String s) {
        return modify(s1 -> s1.replaceFirst("(?i)" + s, ""));
    }

    public BlobMessageModder<T> removeLastIgnoreCase(String s) {
        return modify(s1 -> s1.replaceFirst("(?i)" + s + "(?!.*" + s + ")", ""));
    }

    public BlobMessageModder<T> removeVowels() {
        return modify(s -> s.replaceAll("[aeiouAEIOU]", ""));
    }

    public T get() {
        return (T) blobMessage;
    }
}
