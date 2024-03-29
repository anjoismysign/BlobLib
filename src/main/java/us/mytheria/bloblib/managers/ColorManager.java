package us.mytheria.bloblib.managers;

import org.bukkit.ChatColor;
import us.mytheria.bloblib.BlobLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ColorManager {
    private final Random random;
    private final List<ChatColor> chatcolor;

    public ColorManager() {
        random = new Random();
        chatcolor = new ArrayList<>();
        populate();
    }

    private void populate() {
        chatcolor.addAll(Arrays.asList(ChatColor.values()));
    }

    public ChatColor randomColor() {
        return chatcolor.get(randomInt(chatcolor.size() - 1));
    }

    private int randomInt(int max) {
        return random.nextInt(max + 1);
    }

    public static ChatColor getRandomColor() {
        return BlobLib.getInstance().getColorManager().randomColor();
    }
}
