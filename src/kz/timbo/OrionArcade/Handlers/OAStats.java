package kz.timbo.OrionArcade.Handlers;

import kz.timbo.OrionArcade.OACore;
import kz.timbo.OrionArcade.Objects.OAPlayer;
import me.confuser.barapi.BarAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scoreboard.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OAStats implements Listener {

    private static String primaryColor = ChatColor.DARK_AQUA + "";
    private static String messagePrefix = OACore.generatePrefix(primaryColor + ChatColor.ITALIC + "Stats");

    private static String permPrefix = OACore.getPermPrefix() + "stats.";

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {

        if(event.getPlugin() == OACore.getPlugin()) {

            for(Player player : Bukkit.getOnlinePlayers()) {

                String uuid = player.getUniqueId().toString();
                OAPlayer oaPlayer = new OAPlayer(player);

                // Checking player on login
                try {

                    Statement statement = OACore.getConnection().createStatement();
                    ResultSet res = statement.executeQuery("SELECT * FROM players WHERE uuid = '" + uuid + "' LIMIT 0, 1;");

                    String lastVisit = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());

                    if(res.next()) {

                        if(res.getBoolean("is_banned")) {

                            player.kickPlayer(messagePrefix + primaryColor + "You are banned from " + OACore.getPluginName() + ChatColor.RESET + ".");
                            continue;

                        }

                        oaPlayer.setGems(res.getInt("gems"));
                        oaPlayer.setCrystals(res.getInt("crystals"));
                        statement.executeUpdate("UPDATE players SET last_visit = '" + lastVisit + "' WHERE uuid = '" + uuid + "';");

                    } else {

                        int gems = 50;
                        int crystals = 5;

                        statement.execute("INSERT INTO players(uuid, name, joined, last_visit, gems, crystals)" +
                                "VALUES('" + uuid + "', '" + player.getDisplayName() + "', '" + lastVisit + "', '" + lastVisit + "', '" + gems + "', '" + crystals + "');");

                        oaPlayer.setGems(gems);
                        oaPlayer.setCrystals(crystals);
                        oaPlayer.setFirstJoin(true);

                    }

                } catch (SQLException e) {

                    OACore.getConsole().sendMessage(messagePrefix + primaryColor + "Error: " + ChatColor.RESET + "Can't create MySQL statement!");
                    OACore.getConsole().sendMessage(messagePrefix + e.getMessage());
                    OAAdmin.notifyAdmins(messagePrefix + primaryColor + "Error: " + ChatColor.RESET + "Can't create MySQL statement on " + player.getDisplayName() + " login!");
                    player.kickPlayer(messagePrefix + primaryColor + "Internal Server Error." + ChatColor.RESET + " Online admins have been notified.");
                    continue;

                }

                OACore.getPlayerManager().addPlayer(oaPlayer);

            }

            OACore.getConsole().sendMessage(messagePrefix + "Stats enabled!");

        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {

        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        OAPlayer oaPlayer = new OAPlayer(player);

        // Checking player on login
        try {

            Statement statement = OACore.getConnection().createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM players WHERE uuid = '" + uuid + "' LIMIT 0, 1;");

            String lastVisit = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
            
            if(res.next()) {

                if(res.getBoolean("is_banned")) {

                    event.setKickMessage(messagePrefix + primaryColor + "You are banned from " + OACore.getPluginName() + ChatColor.RESET + ".");
                    event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
                    return;

                }

                oaPlayer.setGems(res.getInt("gems"));
                oaPlayer.setCrystals(res.getInt("crystals"));
                statement.executeUpdate("UPDATE players SET last_visit = '" + lastVisit + "' WHERE uuid = '" + uuid + "';");

            } else {

                int gems = 50;
                int crystals = 5;

                statement.execute("INSERT INTO players(uuid, name, joined, last_visit, gems, crystals)" +
                                  "VALUES('" + uuid + "', '" + player.getDisplayName() + "', '" + lastVisit + "', '" + lastVisit + "', '" + gems + "', '" + crystals + "');");

                oaPlayer.setGems(gems);
                oaPlayer.setCrystals(crystals);
                oaPlayer.setFirstJoin(true);

            }

        } catch (SQLException e) {

            OACore.getConsole().sendMessage(messagePrefix + primaryColor + "Error: " + ChatColor.RESET + "Can't create MySQL statement!");
            OACore.getConsole().sendMessage(messagePrefix + e.getMessage());
            OAAdmin.notifyAdmins(messagePrefix + primaryColor + "Error: " + ChatColor.RESET + "Can't create MySQL statement on " + player.getDisplayName() + " login!");
            event.setKickMessage(messagePrefix + primaryColor + "Internal Server Error." + ChatColor.RESET + " Online admins have been notified.");
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;

        }

        OACore.getPlayerManager().addPlayer(oaPlayer);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {

        OAPlayer player = OACore.getPlayerManager().getPlayer(event.getPlayer());

        resetPlayer(player);

        for(OAPlayer currentPlayer : OACore.getPlayerManager().getPlayers())
            resetPlayerScoreboard(currentPlayer);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        OACore.getPlayerManager().removePlayer(event.getPlayer());

        for(OAPlayer player : OACore.getPlayerManager().getPlayers())
            resetPlayerScoreboard(player);

    }

    public static Scoreboard getDefaultScoreboard(OAPlayer player) {

        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("sidebar", "dummy");
        objective.setDisplayName(player.getShortDisplayName());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        genScoreboard(
                objective,
                new String[] {
                        "",
                        ChatColor.GREEN + "Gems",
                        ChatColor.GREEN + "" + player.getGems(),
                        "",
                        ChatColor.AQUA + "Crystals",
                        ChatColor.AQUA + "" + player.getCrystals(),
                        "",
                        ChatColor.GOLD + "Players",
                        ChatColor.GOLD + "Online",
                        ChatColor.GOLD + "" + Bukkit.getOnlinePlayers().length,
                        ""
                }
        );

        return scoreboard;

    }

    public static void genScoreboard(Objective objective, List<String> lines) {

        String[] linesArray = new String[lines.size()];

        lines.toArray(linesArray);

        genScoreboard(objective, linesArray);

    }

    public static void genScoreboard(Objective objective, String[] lines) {

        int spaceCounter = 0;
        for(int i = 0; i < lines.length; i++) {
            String content = lines[i];
            if(content == null || content.equalsIgnoreCase("") || content.isEmpty()) {
                content = ChatColor.RED + StringUtils.repeat(" ", 14 - spaceCounter);
                spaceCounter++;
            }
            if(content.length() > 16)
                content = content.substring(0, 16);
            Score line = objective.getScore(content);
            line.setScore(lines.length - i);
            if(i >= 15) {
                break;
            }
        }
    }

    public static void resetPlayer(OAPlayer player, Location location, PlayerTeleportEvent.TeleportCause teleportCause) {

        resetPlayer(player);

        Player bukkitPlayer = player.getPlayer();
        bukkitPlayer.teleport(location, teleportCause);

    }

    public static void resetPlayer(OAPlayer player) {

        Player bukkitPlayer = player.getPlayer();

        if(player.getPermUser().inGroup("Mod", true))
            bukkitPlayer.setAllowFlight(true);

        bukkitPlayer.setFoodLevel(20);
        bukkitPlayer.setHealth(20);
        bukkitPlayer.setGameMode(GameMode.SURVIVAL);
        bukkitPlayer.setFireTicks(0);
        player.setInShop(false);
        player.setInGame(false);
        player.setCurrentGame(null);
        player.setListName(player.getShortDisplayName());

        resetPlayerScoreboard(player);

    }

    public static void resetPlayerScoreboard(OAPlayer player) {

        if(player.isInGame() || player.isInShop())
            return;

        Player bukkitPlayer = player.getPlayer();

        bukkitPlayer.setScoreboard(getDefaultScoreboard(player));
        BarAPI.setMessage(bukkitPlayer, OACore.getOrion() + " " + OACore.getArcade() + ChatColor.GRAY + " [" + ChatColor.GREEN + Bukkit.getOnlinePlayers().length + ChatColor.GRAY + "/" + Bukkit.getMaxPlayers() + "]");

    }

}
