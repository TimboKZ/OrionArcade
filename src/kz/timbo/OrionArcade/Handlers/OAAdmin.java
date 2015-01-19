package kz.timbo.OrionArcade.Handlers;

import kz.timbo.OrionArcade.Handlers.OAPermission;
import kz.timbo.OrionArcade.OACore;
import kz.timbo.OrionArcade.Objects.OAPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.PluginEnableEvent;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;

import java.util.ArrayList;
import java.util.List;

public class OAAdmin implements CommandExecutor {

    private static String primaryColor = ChatColor.AQUA + "";
    private static String messagePrefix = OACore.generatePrefix(primaryColor + ChatColor.ITALIC + "Admin");

    private static String permPrefix = OACore.getPermPrefix() + "admin.";

    private static List<Player> admins = new ArrayList<Player>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPluginEnable(PluginEnableEvent event) {

        if(event.getPlugin() == OACore.getPlugin()) {

            for(PermissionGroup group : OAPermission.getPex().getGroups("Mod", true)) {
                for(PermissionUser admin: group.getUsers()) {
                    // TODO: Change to UUID
                    admins.add(Bukkit.getPlayer(admin.getName()));
                }
            }

            OACore.getConsole().sendMessage(messagePrefix + "Admin Panel enabled!");

        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("admin")) {

            boolean isPlayer = sender instanceof Player;
            OAPlayer player;

            if(isPlayer) {
                player = OACore.getPlayerManager().getPlayer((Player) sender);

                if(!player.getPermUser().has(permPrefix + "base")) {
                    sender.sendMessage(messagePrefix + "You're not allowed to use " + primaryColor + "Admin" + ChatColor.RESET + " commands.");
                    sender.sendMessage(permPrefix + "base");
                    return true;
                }
            }

            List<Object[]> commands = new ArrayList<Object[]>();
            // Structure: [Label], [Description], [Player Only]
            commands.add(new Object[] { "ban " + primaryColor + "<name>", "Bans specified player.", false });
            commands.add(new Object[] { "fly", "Toggles flying.", true });
            commands.add(new Object[] { "genpos", "Outputs current position in console.", true });
            commands.add(new Object[] { "kick " + primaryColor + "<name>", "Kicks specified player.", false });
            commands.add(new Object[] { "tp " + primaryColor + "<name> ", "Teleports you to specified player.", true });
            commands.add(new Object[] { "tpo " + primaryColor + "<name> <name>", "Teleports one player to another.", false });

            if(args.length == 0 || args[0].equalsIgnoreCase("help")) {

                if(isPlayer)
                    sender.sendMessage(messagePrefix + primaryColor + "Player " + ChatColor.RESET + "commands:");
                else
                    sender.sendMessage(messagePrefix + primaryColor + "Console " + ChatColor.RESET + "commands:");

                for(Object[] objects : commands) {

                    if(isPlayer || !(Boolean) objects[2]) {

                        sender.sendMessage(OACore.getPrefixOpenBracket() + OACore.getPrefixCloseBracket() + ChatColor.RESET + " " + (String) objects[0] + " " + OACore.getMiscColor() + (String) objects[1]);

                    }

                }

                sender.sendMessage(messagePrefix + "End of list.");

            } else {

                if(args[0].equalsIgnoreCase("ban")) {

                    if(args.length == 2) {



                    } else {

                        sender.sendMessage(messagePrefix + "Invalid amount of arguments.");

                    }

                }
                if(args[0].equalsIgnoreCase("genpos")) {

                    if(sender instanceof Player) {

                        Location location = ((Player) sender).getLocation();

                        OACore.getConsole().sendMessage("Location(OACore.getWorld(), " +
                                "" + (double) Math.round(location.getX() * 10) / 10 + ", " +
                                "" + (double) Math.round(location.getY() * 10) / 10 + ", " +
                                "" + (double) Math.round(location.getZ() * 10) / 10 + ", " +
                                "" + (double) Math.round(location.getYaw() * 10) / 10 + "f, " +
                                "" + (double) Math.round(location.getPitch() * 10) / 10 + "f);");

                        sender.sendMessage(messagePrefix + "Position (Location) string generated! Look it up in console.");

                    } else {

                        sender.sendMessage(messagePrefix + "This command can only be used by players.");

                    }

                }

            }

        }

        return true;

    }

    public static void notifyAdmins(String string) {

        for(Player admin : admins) {
            if(admin.isOnline()) {
                admin.sendMessage(messagePrefix + primaryColor + "Notification: " + ChatColor.RESET + string);
            }
        }

    }

}
