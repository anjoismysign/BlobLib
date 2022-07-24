package us.mytheria.bloblib.jlib.methods;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class General {
  public static void sendColoredMessage(JavaPlugin plugin, String message, ChatColor color) {
    ConsoleCommandSender c = plugin.getServer().getConsoleSender();
    c.sendMessage("[" + plugin.getDescription().getName() + "] " + color + message);
  }
  
  public static void sendMessage(JavaPlugin plugin, String message) {
    sendColoredMessage(plugin, message, ChatColor.RESET);
  }
  
  public static int roundUp(int from, int to) {
    return (from + to - 1) / to * to;
  }
  
  public static void freezePlayer(Player player) {
    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2147483647, 128, true, false));
    player.setWalkSpeed(0.0F);
  }
  
  public static void unfreezePlayer(Player player) {
    player.removePotionEffect(PotionEffectType.JUMP);
    player.setWalkSpeed(0.2F);
  }
  
  public static boolean isFrozen(Player player) {
    return (player.getWalkSpeed() == 0.0F && player.hasPotionEffect(PotionEffectType.JUMP));
  }
}
