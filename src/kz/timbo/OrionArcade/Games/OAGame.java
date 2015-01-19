package kz.timbo.OrionArcade.Games;

import kz.timbo.OrionArcade.Handlers.OAStats;
import kz.timbo.OrionArcade.OACore;
import kz.timbo.OrionArcade.Objects.OAGameType;
import kz.timbo.OrionArcade.Objects.OAPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class OAGame {

    private OAGameType type;
    private int ID;
    private String mapName;
    private int size;

    private List<OAPlayer> players;
    private Location signLocation;
    private Location[] mapBounds;

    private String messagePrefix;
    private String errorString;

    public OAGame(OAGameType type, int ID, String mapName, int size, Location signLocation, Location[] mapBounds) {
        this.type = type;
        this.ID = ID;
        this.mapName = mapName;
        this.size = size;

        this.players = new ArrayList<OAPlayer>();
        this.signLocation = signLocation;
        this.mapBounds = mapBounds;

        this.messagePrefix = type.getMessagePrefix();
        this.errorString = OACore.getErrorString();

        this.prepareGame();
        this.updateSign();
    }

    public abstract void prepareGame();
    public abstract void updateSign();
    public abstract void updateScoreboards();

    public void joinGame(OAPlayer player) {
        if(!player.isInShop()) {
            if(!player.isInGame()) {
                if(players.size() < size) {
                    if(player.getPermUser().has(type.getPerm())) {
                        if(this.enterGame(player)) {
                            players.add(player);
                            OAStats.resetPlayer(player);
                            player.setInGame(true);
                            player.setCurrentGame(this);
                            player.setListName(type.getColor() + "[" + type.getShortName() + "]" + player.getShortDisplayName());

                            Player bukkitPlayer = player.getPlayer();
                            bukkitPlayer.setAllowFlight(false);

                            this.updateScoreboards();
                            this.updateSign();
                        }
                        return;
                    }
                    player.sendMessage(messagePrefix + errorString + "You're not allowed to play this game!");
                    return;
                }
                player.sendMessage(messagePrefix + errorString + "Game is full!");
                return;
            }
            player.sendMessage(messagePrefix + errorString + "You're already in a game!");
            return;
        }
        player.sendMessage(messagePrefix + errorString + "You're in a shop!");
        return;
    }

    public abstract boolean enterGame(OAPlayer player);

    public void leaveGame(OAPlayer player) {
        if(players.contains(player)) {
            if(this.exitGame(player)) {
                players.remove(player);
                OAStats.resetPlayer(player, type.getLobbyLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                this.updateScoreboards();
                this.updateSign();
            }
            return;
        }
        player.sendMessage(messagePrefix + errorString + "Wrong game attached to the player. Try again.");
        player.setCurrentGame(null);
        player.setInGame(false);
        return;
    }

    public abstract boolean exitGame(OAPlayer player);

    public abstract void abandonGame(OAPlayer player);

    public void stopGame(String reason) {

        if(reason.equalsIgnoreCase("reload"))
            notifyPlayers("The game has been stopped due to server reload.");


        for(OAPlayer player : players)
            OAStats.resetPlayer(player, type.getLobbyLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);

        players.clear();

    }

    public void notifyPlayers(String message) {

        notifyPlayers(null, message);

    }

    public void notifyPlayers(OAPlayer player, String message) {

        if(player == null)
            for(OAPlayer currentPlayer : players) {
                currentPlayer.sendMessage(type.getMessagePrefix() + message);
                currentPlayer.playSound("notifyBad");
            }

        else
            for(OAPlayer currentPlayer : players)
                if(currentPlayer != player) {
                    currentPlayer.sendMessage(type.getMessagePrefix() + message);
                    currentPlayer.playSound("notifyBad");
                }

    }

    public OAGameType getType() {
        return type;
    }

    public int getID() {
        return ID;
    }

    public String getMapName() {
        return mapName;
    }

    public int getSize() {
        return size;
    }

    public List<OAPlayer> getPlayers() {
        return players;
    }

    public Location getSignLocation() {
        return signLocation;
    }

    public Location[] getMapBounds() {
        return mapBounds;
    }

    public String getMessagePrefix() {
        return messagePrefix;
    }

    public String getErrorString() {
        return errorString;
    }
}
