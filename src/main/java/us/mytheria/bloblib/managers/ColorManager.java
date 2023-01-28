package us.mytheria.bloblib.managers;

import net.md_5.bungee.api.ChatColor;
import us.mytheria.bloblib.BlobLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorManager {
    private final List<ChatColor> chatcolor;

    public ColorManager() {
        chatcolor = new ArrayList<>();
    }

    private void fill() {
        chatcolor.addAll(Arrays.asList(ChatColor.values()));
    }

    public ChatColor randomColor() {
        return chatcolor.get((int) (Math.random() * chatcolor.size()));
    }

    public static ChatColor getRandomColor() {
        return BlobLib.getInstance().getColorManager().randomColor();
    }
}
