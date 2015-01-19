package kz.timbo.OrionArcade.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OAPlayerManager {

    private static HashMap<String, OAPlayer> players = new HashMap<String, OAPlayer>();

    public OAPlayerManager() {
        for(Player player : Bukkit.getOnlinePlayers())
            if(players.get(player.getDisplayName()) == null)
                players.put(player.getDisplayName(), new OAPlayer(player));
    }

    public static void addPlayer(Player player) {
        players.put(player.getUniqueId().toString(), new OAPlayer(player));
    }

    public static void addPlayer(OAPlayer player) {
        players.put(player.getUuid().toString(), player);
    }

    public static OAPlayer getPlayer(Player player) {
        return players.get(player.getUniqueId().toString());
    }

    public static void removePlayer(Player player) {
        players.remove(player.getUniqueId().toString());
    }

    public static List<OAPlayer> getPlayers() {

        List<OAPlayer> playerList = new ArrayList<OAPlayer>();
        playerList.addAll(players.values());
        return playerList;

    }

}
