package kz.timbo.OrionArcade;

import code.husky.mysql.MySQL;
import kz.timbo.OrionArcade.Handlers.*;
import kz.timbo.OrionArcade.Objects.OAGameType;
import kz.timbo.OrionArcade.Objects.OAPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;

public class OACore extends JavaPlugin {

    private static boolean debugging = false;

    // Plugin name and prefixes
    private static String orion = ChatColor.AQUA + "O" + ChatColor.DARK_AQUA + "ri" + ChatColor.BLUE + "o" + ChatColor.DARK_BLUE + "n";
    private static String arcade = ChatColor.LIGHT_PURPLE + "A" + ChatColor.DARK_PURPLE + "r" + ChatColor.DARK_RED + "c" + ChatColor.RED + "a" + ChatColor.GOLD + "d" + ChatColor.YELLOW + "e";
    private static String pluginName = orion + " " + arcade;
    private static String pluginShortName = ChatColor.AQUA + "" +ChatColor.BOLD + "O" + ChatColor.LIGHT_PURPLE + "" +ChatColor.BOLD + "A";
    private static String prefixOpenBracket = ChatColor.DARK_GRAY + "[";
    private static String prefixCloseBracket = ChatColor.DARK_GRAY + "]";
    private static String messagePrefix = prefixOpenBracket + pluginName + prefixCloseBracket + " " + ChatColor.WHITE;
    private static String messageShortPrefix = prefixOpenBracket + pluginShortName + prefixCloseBracket + " " + ChatColor.WHITE;
    private static String permPrefix = "oa.";
    private static String errorString = ChatColor.RED + "Error: " + ChatColor.RESET;

    // Colors
    private static String primaryColor = ChatColor.AQUA + "";
    private static String secondaryColor = ChatColor.YELLOW + "";
    private static String miscColor = ChatColor.GRAY + "";

    // Plugin, world, console for debugging etc.
    private static JavaPlugin plugin;
    private static CommandSender console;
    private static OAPlayerManager playerManager;
    private static String worldName = "OrionArcade";
    private static World world;
    private static Location gameSpawn;

    // MySQL Stuff
    private static MySQL mySQL = new MySQL(plugin, "localhost", "3306", "OrionArcade", "root", "");
    private static Connection connection = null;

    @Override
    public void onEnable() {

        plugin = this;
        console = Bukkit.getConsoleSender();
        console.sendMessage(messagePrefix + "Loading the plugin . . .");
        /*************************************************/

        world = Bukkit.getWorld(OACore.getWorldName());
        gameSpawn = new Location(world, -300.5, 36.0, 1015.5, 0.0f, 90.0f);

        // Establishing MySQL connection
        connection = mySQL.openConnection();

        playerManager = new OAPlayerManager();

        // Registering event listeners
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new OAStats(), this);
        pluginManager.registerEvents(new OAProtect(), this);
        pluginManager.registerEvents(new OAGames(), this);
        pluginManager.registerEvents(new OAChat(), this);

        // Registering commands
        getCommand("admin").setExecutor(new OAAdmin());

        for(OAGameType gameType : OAGameType.values())
            getCommand(gameType.getCommandName()).setExecutor(new OAGames());

        getCommand("leave").setExecutor(new OAGames());

        /*************************************************/
        console.sendMessage(messagePrefix + "Core enabled!");

    }

    @Override
    public void onDisable() {

        console.sendMessage(messageShortPrefix + "Core disabled!");

    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static boolean isDebugging() {
        return debugging;
    }

    public static String getOrion() {
        return orion;
    }

    public static String getArcade() {
        return arcade;
    }

    public static String getPluginName() {
        return pluginName;
    }

    public static String getPluginShortName() {
        return pluginShortName;
    }

    public static String getMessagePrefix() {
        return messagePrefix;
    }

    public static String getMessageShortPrefix() {
        return messageShortPrefix;
    }

    public static String generatePrefix(String string) {

        return prefixOpenBracket+ pluginShortName + ChatColor.RESET + ": " + string + prefixCloseBracket + " " + ChatColor.WHITE;

    }

    public static String getPrefixOpenBracket() {
        return prefixOpenBracket;
    }

    public static String getPrefixCloseBracket() {
        return prefixCloseBracket;
    }

    public static String getPermPrefix() {
        return permPrefix;
    }

    public static String getErrorString() {
        return errorString;
    }

    public static String getPrimaryColor() {
        return primaryColor;
    }

    public static String getSecondaryColor() {
        return secondaryColor;
    }

    public static String getMiscColor() {
        return miscColor;
    }

    public static CommandSender getConsole() {
        return console;
    }

    public static OAPlayerManager getPlayerManager() {
        return playerManager;
    }

    public static String getWorldName() {
        return worldName;
    }

    public static World getWorld() {
        return world;
    }

    public static Location getGameSpawn() {
        return gameSpawn;
    }

    public static Connection getConnection() { return connection; }
}
