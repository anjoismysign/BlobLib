package us.mytheria.bloblib.jlib.logging;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

public final class JLogger {
  private final Plugin plugin;
  
  public JLogger(Plugin plugin) {
    this.plugin = plugin;
  }
  
  public JLogger() {
    this.plugin = Bukkit.getPluginManager().getPlugin("JLib");
  }
  
  public void log(String message) {
    ConsoleCommandSender c = this.plugin.getServer().getConsoleSender();
    c.sendMessage("[" + this.plugin.getDescription().getName() + "] " + message);
  }
  
  public void debug() {
    log("" + ChatColor.DARK_PURPLE + "DEBUG: " + ChatColor.DARK_PURPLE);
  }
  
  public void debug(String data) {
    log("" + ChatColor.DARK_PURPLE + "DEBUG: " + ChatColor.DARK_PURPLE);
    log("" + ChatColor.DARK_PURPLE + "DEBUG: " + ChatColor.DARK_PURPLE);
  }
  
  public void deprecation() {
    StackTraceElement[] stackTraceElements = (new Exception()).getStackTrace();
    log("" + ChatColor.GOLD + "WARNING: I'm using deprecated method " + ChatColor.GOLD + " at " + stackTraceElements[1] + "!");
  }
  
  public void warnIfSync() {
    if (Bukkit.isPrimaryThread()) {
      StackTraceElement[] stackTraceElements = (new Exception()).getStackTrace();
      log("" + ChatColor.GOLD + "WARNING: " + ChatColor.GOLD + " should be ran Async! (at " + stackTraceElements[1] + ")!");
    } 
  }
  
  public void warnIfAsync() {
    if (!Bukkit.isPrimaryThread()) {
      StackTraceElement[] stackTraceElements = (new Exception()).getStackTrace();
      log("" + ChatColor.GOLD + "WARNING: " + ChatColor.GOLD + " should be ran Sync! (at " + stackTraceElements[1] + ")!");
    } 
  }
}
