package kz.timbo.OrionArcade.Games;


import kz.timbo.OrionArcade.Games.Interfaces.OAPvP;
import kz.timbo.OrionArcade.Handlers.OAStats;
import kz.timbo.OrionArcade.OACore;
import kz.timbo.OrionArcade.OAUtil;
import kz.timbo.OrionArcade.Objects.OAGameType;
import kz.timbo.OrionArcade.Objects.OAPlayer;
import kz.timbo.OrionArcade.Games.Interfaces.OARespawnable;
import me.confuser.barapi.BarAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

public class OADeathmatch extends OAGame implements OAPvP, OARespawnable {

    private Location[] spawnPoints;
    private HashMap<OAPlayer, Scoreboard> scoreboards;

    private HashMap<OAPlayer, Integer> kills;
    private HashMap<OAPlayer, Integer> deaths;

    public OADeathmatch(int gameID, String mapName, int gameSize, Location signLocation, Location[] mapBounds, Location[] spawnPoints) {

        super(OAGameType.DEATHMATCH, gameID, mapName, gameSize, signLocation, mapBounds);
        this.spawnPoints = spawnPoints;
        this.scoreboards = new HashMap<OAPlayer, Scoreboard>();

        this.kills = new HashMap<OAPlayer, Integer>();
        this.deaths = new HashMap<OAPlayer, Integer>();

    }

    @Override
    public void prepareGame() {



    }

    @Override
    public void updateSign() {

        Block block = OACore.getWorld().getBlockAt(getSignLocation());

        if(!OAUtil.isSign(block))
            return;

        Sign sign = (Sign) block.getState();

        sign.setLine(0, ChatColor.BLUE + "#" + getID() + ":" + " " + ChatColor.YELLOW + getMapName());

        int count = getPlayers().size();
        String countColor = ChatColor.GREEN + "";
        if(count == getSize()) {
            countColor = ChatColor.RED + "";
        }

        sign.setLine(1, countColor + count + ChatColor.DARK_GRAY + "/" + getSize());

        if(count == getSize()) {
            sign.setLine(2, ChatColor.RED + "Full");
            sign.setLine(3, "");
        } else if(count > 0) {
            sign.setLine(2, ChatColor.GREEN + "Playing...");
            sign.setLine(3, "");
        } else {
            sign.setLine(2, ChatColor.GREEN + "Waiting for");
            sign.setLine(3, ChatColor.GREEN + "players...");
        }

        sign.update();

    }

    @Override
    public void updateScoreboards() {

        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

        for(OAPlayer player : getPlayers()) {

            if(scoreboards.get(player) == null) {
                scoreboards.put(player, scoreboardManager.getNewScoreboard());
            }

            Scoreboard scoreboard = scoreboards.get(player);
            Objective objective = scoreboard.getObjective("stats");

            if(objective != null) {
                objective.unregister();
            }

            objective = scoreboard.registerNewObjective("stats", "dummy");
            objective.setDisplayName("Top Scorers");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            List<String> lines = new ArrayList<String>();

            for(Map.Entry<OAPlayer, Integer> entrySet : OAUtil.sortByValue(kills)) {
                OAPlayer currentPlayer = entrySet.getKey();
                lines.add(currentPlayer.getPlayer().getDisplayName());
                lines.add(ChatColor.GREEN + "K" + entrySet.getValue() + ChatColor.RED + " D" + deaths.get(currentPlayer));
            }

            lines.add("");
            lines.add(" Your Stats");

            if(kills.get(player) == 1)
                lines.add(ChatColor.GREEN + "" + kills.get(player) + " Kill");
            else
                lines.add(ChatColor.GREEN + "" + kills.get(player) + " Kills");

            if(deaths.get(player) == 1)
                lines.add(ChatColor.RED + "" + deaths.get(player) + " Death");
            else
                lines.add(ChatColor.RED + "" + deaths.get(player) + " Deaths");

            OAStats.genScoreboard(objective, lines);

            Player bukkitPlayer = player.getPlayer();

            bukkitPlayer.setScoreboard(scoreboard);

            OAGameType gameType = getType();

            BarAPI.setMessage(bukkitPlayer, gameType.getColor() + gameType.getName() + ChatColor.RESET + " on map " + gameType.getColor() + getMapName() + ChatColor.GRAY + " [" + ChatColor.GREEN + getPlayers().size() + ChatColor.GRAY + "/" + getSize() + "]");

        }

    }

    @Override
    public boolean enterGame(OAPlayer player) {

        Random random = new Random();

        player.getPlayer().teleport(spawnPoints[random.nextInt(spawnPoints.length)], PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.sendMessage(getMessagePrefix() + "You've joined the game on map " + getType().getColor() + getMapName() + ChatColor.GRAY + " (#" + getID() + ")" + ChatColor.RESET + ".");
        notifyPlayers(player, player.getShortDisplayName() + ChatColor.RESET + " has joined the game.");
        kills.put(player, 0);
        deaths.put(player, 0);

        return true;

    }

    @Override
    public boolean exitGame(OAPlayer player) {

        player.sendMessage(getMessagePrefix() + "You've left the game on map " + getType().getColor() + getMapName() + ChatColor.GRAY + " (#" + getID() + ")" + ChatColor.RESET + ".");
        notifyPlayers(player, player.getShortDisplayName() + ChatColor.RESET + " has left the game.");
        kills.remove(player);
        deaths.remove(player);
        return true;

    }

    @Override
    public void abandonGame(OAPlayer player) {

        notifyPlayers(player, player.getShortDisplayName() + ChatColor.RESET + " has abandoned the game.");
        OAStats.resetPlayer(player, getType().getHubLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        getPlayers().remove(player);
        kills.remove(player);
        deaths.remove(player);
        updateScoreboards();
        updateSign();

    }

    public boolean isMeleeAllowed() {

        return true;

    }

    public boolean isBowAllowed() {

        return false;

    }

    public Location getSpawnPoint(OAPlayer player) {
        Random random = new Random();
        return spawnPoints[random.nextInt(spawnPoints.length)];
    }

    public void addKills(OAPlayer player) {

        if(kills.get(player) == null)
            kills.put(player, 0);

        kills.put(player, kills.get(player) + 1);

        if(player.getPlayer().getHealth() != 20) {

            if(player.getPlayer().getHealth() + 6 < 20)
                player.getPlayer().setHealth(player.getPlayer().getHealth() + 6);
            else
                player.getPlayer().setHealth(20);

        }

        updateScoreboards();

    }

    public void addDeaths(OAPlayer player) {

        if(deaths.get(player) == null)
            deaths.put(player, 0);

        deaths.put(player, deaths.get(player) + 1);

        updateScoreboards();

    }

}
