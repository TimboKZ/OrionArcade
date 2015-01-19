package kz.timbo.OrionArcade.Games;

import kz.timbo.OrionArcade.Games.Interfaces.OARespawnable;
import kz.timbo.OrionArcade.Handlers.OAStats;
import kz.timbo.OrionArcade.OACore;
import kz.timbo.OrionArcade.OAUtil;
import kz.timbo.OrionArcade.Objects.OAGameType;
import kz.timbo.OrionArcade.Objects.OAPlayer;
import me.confuser.barapi.BarAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class OAParkour extends OAGame {

    private HashMap<OAPlayer, Scoreboard> scoreboards;

    private String difficulty;
    private Location firstCheckpoint;

    private HashMap<OAPlayer, Integer> checkpointIDs;
    private HashMap<OAPlayer, Location> checkpoints;
    private HashMap<OAPlayer, Long> joined;
    private HashMap<OAPlayer, Integer> deaths;

    public OAParkour(int gameID, String mapName, int gameSize, Location signLocation, Location[] mapBounds, String difficulty, Location firstCheckpoint) {

        super(OAGameType.PARKOUR, gameID, mapName, gameSize, signLocation, mapBounds);

        this.difficulty = difficulty;
        this.firstCheckpoint = firstCheckpoint;

        this.scoreboards = new HashMap<OAPlayer, Scoreboard>();
        this.checkpointIDs = new HashMap<OAPlayer, Integer>();
        this.checkpoints = new HashMap<OAPlayer, Location>();
        this.joined = new HashMap<OAPlayer, Long>();
        this.deaths = new HashMap<OAPlayer, Integer>();

        updateSign();

    }

    @Override
    public void prepareGame() {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(OACore.getPlugin(), new Runnable() {
            @Override
            public void run() {
                updateScoreboards();
            }
        }, 0L, 20L);

    }

    @Override
    public void updateSign() {

        Block block = OACore.getWorld().getBlockAt(getSignLocation());

        if(!OAUtil.isSign(block))
            return;

        Sign sign = (Sign) block.getState();

        sign.setLine(0, ChatColor.RED + getMapName());

        int count = getPlayers().size();
        String countColor = ChatColor.GREEN + "";
        if(count == getSize()) {
            countColor = ChatColor.RED + "";
        }

        sign.setLine(1, countColor + count + ChatColor.DARK_GRAY + "/" + getSize());
        sign.setLine(2, ChatColor.BLUE + difficulty);
        sign.setLine(3, ChatColor.DARK_GRAY + "Track #" + getID());

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
            objective.setDisplayName("Stats");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            List<String> lines = new ArrayList<String>();

            lines.add("");

            lines.add(ChatColor.GOLD + "Current");
            lines.add(ChatColor.GOLD + "Checkpoint");
            lines.add(ChatColor.GOLD + "" + checkpointIDs.get(player));

            lines.add("");

            long time = System.currentTimeMillis() - joined.get(player);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
            long seconds = (TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MILLISECONDS.toMinutes(time) * 60);

            String minuteString = "" + minutes;
            String secondString = "" + seconds;

            if(minuteString.length() < 2)
                minuteString = StringUtils.repeat("0", 2 - minuteString.length()) + minuteString;

            if(secondString.length() < 2)
                secondString = StringUtils.repeat("0", 2 - secondString.length()) + secondString;

            lines.add(ChatColor.AQUA + "Time on");
            lines.add(ChatColor.AQUA + "the Map");
            lines.add(ChatColor.AQUA + "" + minuteString + ":" + secondString);

            lines.add("");

            lines.add(ChatColor.RED + "Deaths");
            lines.add(ChatColor.RED + "" + deaths.get(player));

            lines.add("");

            OAStats.genScoreboard(objective, lines);

            Player bukkitPlayer = player.getPlayer();

            bukkitPlayer.setScoreboard(scoreboard);

            OAGameType gameType = getType();

            BarAPI.setMessage(bukkitPlayer, gameType.getColor() + gameType.getName() + ChatColor.RESET + " on map " + gameType.getColor() + getMapName() + ChatColor.GRAY + " (" + difficulty + ") [" + ChatColor.GREEN + getPlayers().size() + ChatColor.GRAY + "/" + getSize() + "]");

        }

    }

    @Override
    public boolean enterGame(OAPlayer player) {

        player.getPlayer().teleport(firstCheckpoint, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.sendMessage(getMessagePrefix() + "You've entered the track " + getType().getColor() + getMapName() + ChatColor.GRAY + " (#" + getID() + ")" + ChatColor.RESET + ".");
        notifyPlayers(player, player.getShortDisplayName() + ChatColor.RESET + " has entered the track.");

        joined.put(player, System.currentTimeMillis());
        deaths.put(player, 0);

        checkpointIDs.put(player, 1);
        checkpoints.put(player, firstCheckpoint);

        return true;

    }

    @Override
    public boolean exitGame(OAPlayer player) {

        player.sendMessage(getMessagePrefix() + "You've left the track " + getType().getColor() + getMapName() + ChatColor.GRAY + " (#" + getID() + ")" + ChatColor.RESET + ".");
        notifyPlayers(player, player.getShortDisplayName() + ChatColor.RESET + " has left the track.");

        joined.remove(player);
        deaths.remove(player);

        checkpointIDs.remove(player);
        checkpoints.remove(player);
        return true;

    }

    @Override
    public void abandonGame(OAPlayer player) {

        notifyPlayers(player, player.getShortDisplayName() + ChatColor.RESET + " has abandoned the track.");
        OAStats.resetPlayer(player, getType().getHubLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        getPlayers().remove(player);

        joined.remove(player);
        deaths.remove(player);

        checkpointIDs.remove(player);
        checkpoints.remove(player);
        updateScoreboards();
        updateSign();

    }

    public void reset(OAPlayer player) {

        Player bukkitPlayer = player.getPlayer();

        bukkitPlayer.teleport(checkpoints.get(player));
        bukkitPlayer.setFallDistance(0.0f);
        bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.SPLASH, 1, 1);

        deaths.put(player, deaths.get(player) + 1);
        updateSign();

    }

    public void setCheckpoint(OAPlayer player, Block block) {

        Sign sign = (Sign) block.getState();

        String idString = ChatColor.stripColor(sign.getLine(1)).replace("#", "");
        int id = Integer.valueOf(idString);

        if(id < checkpointIDs.get(player)) {
            player.sendMessage(getType().getMessagePrefix() + "You've already reached a further checkpoint.");
            player.playSound("notifyBad");
            return;
        } else if(id == checkpointIDs.get(player)) {
            player.sendMessage(getType().getMessagePrefix() + "This is your current checkpoint.");
            player.playSound("notifyBad");
            return;
        }

        Block woolBlock = block.getLocation().subtract(0, 2, 0).getBlock();

        for(int x = block.getX() - 2; x < block.getX() + 2; x++)
            for(int z = block.getZ() - 2; z < block.getZ() + 2; z++) {
                woolBlock = player.getPlayer().getWorld().getBlockAt(x, block.getY() - 2, z);
                if(woolBlock.getType() == Material.WOOL)
                    break;
            }

        BlockFace blockFace = BlockFace.EAST;

        if(block.getX() - woolBlock.getX() == 0) {
            if (block.getZ() > woolBlock.getZ())
                blockFace = BlockFace.NORTH;
            else
                blockFace = BlockFace.SOUTH;
        } else {
            if (block.getX() > woolBlock.getX())
                blockFace = BlockFace.WEST;
            else
                blockFace = BlockFace.EAST;
        }

        float yaw = 0.0f;

        if(blockFace == BlockFace.NORTH)
            yaw = 90.0f;
        if(blockFace == BlockFace.EAST)
            yaw = -180.0f;
        if(blockFace == BlockFace.SOUTH)
            yaw = -90.0f;
        if(blockFace == BlockFace.WEST)
            yaw = 0.0f;

        Location checkpoint = woolBlock.getLocation().add(0, 1, 0);
        checkpoint.setYaw(yaw);
        checkpoint.setPitch(0.0f);

        checkpointIDs.put(player, id);
        checkpoints.put(player, checkpoint);

        player.sendMessage(getType().getMessagePrefix() + "Checkpoint saved!");
        player.playSound("notifyGood");

        updateScoreboards();

    }

    public void addDeaths(OAPlayer player) {

        deaths.put(player, deaths.get(player) + 1);

    }

}
