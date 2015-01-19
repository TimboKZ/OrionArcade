package kz.timbo.OrionArcade.Handlers;

import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class OAPermission {

    private static PermissionManager pex = PermissionsEx.getPermissionManager();

    public static PermissionUser getPermissionsUser(Player player) {

        return PermissionsEx.getUser(player);

    }

    public static String getPrefix(Player player) {

        return PermissionsEx.getUser(player).getPrefix();

    }

    public static String getShortPrefix(Player player) {

        return PermissionsEx.getUser(player).getPrefix("short");

    }

    public static PermissionManager getPex() {
        return pex;
    }
}
