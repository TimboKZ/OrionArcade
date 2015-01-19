package kz.timbo.OrionArcade.Objects;

import kz.timbo.OrionArcade.Games.OAGame;
import kz.timbo.OrionArcade.Handlers.OAChat;
import kz.timbo.OrionArcade.Handlers.OAPermission;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;

import java.util.UUID;

public class OAPlayer {

    private Player player;
    private PermissionUser permUser;

    private UUID uuid;
    private String name;
    private String group;
    private String prefix;
    private String shortPrefix;
    private int crystals;
    private int gems;

    private boolean firstJoin;
    private boolean inGame;
    private OAGame currentGame;
    private boolean inShop;

    public OAPlayer(Player player) {

        this.player = player;
        this.permUser = OAPermission.getPermissionsUser(player);

        this.uuid = player.getUniqueId();
        this.name = player.getDisplayName();
        this.group = this.permUser.getGroupsNames()[0];
        this.prefix = this.permUser.getPrefix();
        this.shortPrefix = this.permUser.getPrefix("short");

        this.firstJoin = false;
        this.inGame = false;
        this.inShop = false;

    }

    public void sendMessage(String string) {
        player.sendMessage(string);
    }

    public void playSound(String reason) {

        if(reason.equalsIgnoreCase("notAllowed"))
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0f, 1.0f);
        else if(reason.equalsIgnoreCase("notifyGood"))
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
        else if(reason.equalsIgnoreCase("notifyBad"))
            player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
        else
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);

    }

    public Player getPlayer() {
        return player;
    }

    public PermissionUser getPermUser() {
        return permUser;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return OAChat.parseColors(prefix + name);
    }

    public String getShortDisplayName() {
        return OAChat.parseColors(shortPrefix + name);
    }

    public void setListName(String listName) {
        if(listName.length() > 16)
            listName = listName.substring(0, 16);

        if(player.getPlayerListName().equalsIgnoreCase(listName))
            return;

        player.setPlayerListName(listName);
    }

    public String getGroup() {
        return group;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getShortPrefix() {
        return shortPrefix;
    }

    public boolean isFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(boolean firstJoin) {
        this.firstJoin = firstJoin;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public OAGame getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(OAGame currentGame) {
        this.currentGame = currentGame;
    }

    public boolean isInShop() {
        return inShop;
    }

    public void setInShop(boolean inShop) {
        this.inShop = inShop;
    }

    public int getGems() {
        return gems;
    }

    public void setGems(int gems) {
        this.gems = gems;
    }

    public int getCrystals() {
        return crystals;
    }

    public void setCrystals(int crystals) {
        this.crystals = crystals;
    }
}
