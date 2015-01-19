package kz.timbo.OrionArcade.Objects;

import kz.timbo.OrionArcade.Handlers.OAGames;
import kz.timbo.OrionArcade.OACore;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public enum OAGameType {

    ARROW(
            "Arrow", // Name
            "A", // Short name
            "arrow", // Command
            ChatColor.AQUA, // Color
            OAGames.getPermPrefix() + "arrow", // Permission
            new Location(OACore.getWorld(), 0, 0, 0, 0, 0), // Hub location
            new Location(OACore.getWorld(), 0, 0, 0, 0, 0) // Lobby location
    ),
    DEATHMATCH(
            "Deathmatch",
            "DM",
            "deathmatch",
            ChatColor.GOLD,
            OAGames.getPermPrefix() + "deathmatch",
            new Location(OACore.getWorld(), -300.5, 36.0, 960.5, 180.0f, -7.0f),
            new Location(OACore.getWorld(), -300.5, 36.0, 947.5, 180.0f, 1.5f)
    ),
    GUNGAME(
            "GunGame",
            "GG",
            "gungame",
            ChatColor.AQUA,
            OAGames.getPermPrefix() + "gungame",
            new Location(OACore.getWorld(), 0, 0, 0, 0, 0),
            new Location(OACore.getWorld(), 0, 0, 0, 0, 0)
    ),
    MOB_ARENA(
            "Mob Arena",
            "MA",
            "mobarena",
            ChatColor.AQUA,
            OAGames.getPermPrefix() + "mobarena",
            new Location(OACore.getWorld(), 0, 0, 0, 0, 0),
            new Location(OACore.getWorld(), 0, 0, 0, 0, 0)
    ),
    PARKOUR(
            "Parkour",
            "P",
            "parkour",
            ChatColor.AQUA,
            OAGames.getPermPrefix() + "parkour",
            new Location(OACore.getWorld(), -356.0, 36.0, 1003.5, 178.0f, 1.0f),
            new Location(OACore.getWorld(), -356.0, 36.0, 1003.5, 178.0f, 1.0f)
    ),
    SPLEEF(
            "Spleef",
            "S",
            "spleef",
            ChatColor.GREEN,
            OAGames.getPermPrefix() + "spleef",
            new Location(OACore.getWorld(), -356.0, 36.0, 1031.5, 0.0f, -2.7f),
            new Location(OACore.getWorld(), -356.0, 36.0, 1035.5, 0.0f, -1.2f)
    ),
    TEAM_DEATHMATCH(
            "Team DM",
            "TMDM",
            "teamdeathmatch",
            ChatColor.AQUA,
            OAGames.getPermPrefix() + "teamdm",
            new Location(OACore.getWorld(), 0, 0, 0, 0, 0),
            new Location(OACore.getWorld(), 0, 0, 0, 0, 0)
    );

    private final String name;
    private final String shortName;
    private final String commandName;
    private final String messagePrefix;
    private final String color;
    private final String perm;
    private final Location hubLocation;
    private final Location lobbyLocation;

    OAGameType(String name, String shortName, String commandName, ChatColor color, String perm, Location hubLocation, Location lobbyLocation) {
        this.name = name;
        this.shortName = shortName;
        this.commandName = commandName;
        this.messagePrefix = OACore.generatePrefix(color + "" + ChatColor.ITALIC + name);
        this.color = color + "";
        this.perm = perm;
        this.hubLocation = hubLocation;
        this.lobbyLocation = lobbyLocation;
    }

    public String getName() {
        return name;
    }

    public String getShortName() { return shortName; }

    public String getCommandName() {
        return commandName;
    }

    public String getMessagePrefix() {
        return messagePrefix;
    }

    public String getColor() {
        return color;
    }

    public String getPerm() {
        return perm;
    }

    public Location getHubLocation() {
        return hubLocation;
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

}
