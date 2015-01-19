package kz.timbo.OrionArcade.Handlers;


import kz.timbo.OrionArcade.OACore;
import kz.timbo.OrionArcade.Objects.OAPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OAChat implements Listener {

    private static String primaryColor = ChatColor.GREEN + "";
    private static String messagePrefix = OACore.generatePrefix(primaryColor + ChatColor.ITALIC + "Chat");

    private static String permPrefix = OACore.getPermPrefix() + "admin.";

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event){

        OAPlayer player = OACore.getPlayerManager().getPlayer(event.getPlayer());

        if(!player.getPermUser().has(permPrefix + "base")) {

            player.sendMessage(messagePrefix + "You're not allowed to use " + primaryColor + "Chat" + ChatColor.RESET + ".");
            event.setCancelled(true);

        }

        String message = event.getMessage();

        if(!player.getPermUser().has(permPrefix + "color")) {

            message = parseColors(message);

        }

        message = message.replaceAll("(?i)[o0][r2][i1][o0]n", OACore.getOrion() + ChatColor.RESET);
        message = message.replaceAll("(?i)[a4][r2]c[a4]d[e3]", OACore.getArcade() + ChatColor.RESET);

        event.setFormat(player.getShortDisplayName() + ChatColor.WHITE + " " + message);

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {

        OAPlayer player = OACore.getPlayerManager().getPlayer(event.getPlayer());

        if(player.isFirstJoin()) {

            event.setJoinMessage(null);
            for(Player recipient : Bukkit.getOnlinePlayers()) {
                if(recipient.getUniqueId() != player.getUuid())
                    recipient.sendMessage(OACore.getMessagePrefix() + "Everyone, welcome " + player.getDisplayName() + ChatColor.RESET + " to the server!");
            }

            player.sendMessage(OACore.getMessagePrefix() + "Welcome to the server!");

        } else {

            if(player.getPermUser().inGroup("Mod", true))
                event.setJoinMessage(OACore.getMessagePrefix() + "Hey, " + player.getDisplayName() + ChatColor.WHITE + " is now online!");
            else
                event.setJoinMessage(OACore.getMessagePrefix() + player.getDisplayName() + ChatColor.WHITE + " has joined the server!");

        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {

        OAPlayer player = OACore.getPlayerManager().getPlayer(event.getPlayer());

        if(player.getPermUser().inGroup("Mod", true))
            event.setQuitMessage(OACore.getMessagePrefix() + "Damn, " + player.getDisplayName() + ChatColor.WHITE + " has left the server...");
        else
            event.setQuitMessage(OACore.getMessagePrefix() + player.getDisplayName() + ChatColor.WHITE + " just left the server. " + OACore.getPrimaryColor() + ":(");

    }

    public static String parseColors(String string) {

        string = string.replace("&0", "" + ChatColor.BLACK);
        string = string.replace("&1", "" + ChatColor.DARK_BLUE);
        string = string.replace("&2", "" + ChatColor.DARK_GREEN);
        string = string.replace("&3", "" + ChatColor.DARK_AQUA);
        string = string.replace("&4", "" + ChatColor.DARK_RED);
        string = string.replace("&5", "" + ChatColor.DARK_PURPLE);
        string = string.replace("&6", "" + ChatColor.GOLD);
        string = string.replace("&7", "" + ChatColor.GRAY);
        string = string.replace("&8", "" + ChatColor.DARK_GRAY);
        string = string.replace("&9", "" + ChatColor.BLUE);

        string = string.replace("&a", "" + ChatColor.GREEN);
        string = string.replace("&b", "" + ChatColor.AQUA);
        string = string.replace("&c", "" + ChatColor.RED);
        string = string.replace("&d", "" + ChatColor.LIGHT_PURPLE);
        string = string.replace("&e", "" + ChatColor.YELLOW);
        string = string.replace("&f", "" + ChatColor.WHITE);

        return string;

    }

}
