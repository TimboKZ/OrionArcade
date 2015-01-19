package kz.timbo.OrionArcade.Handlers;

import kz.timbo.OrionArcade.OACore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class OAProtect implements Listener {

    private static String primaryColor = ChatColor.DARK_GREEN + "";
    private static String messagePrefix = OACore.generatePrefix(primaryColor + ChatColor.ITALIC + "Protect");

    private static String permPrefix = OACore.getPermPrefix() + "protect.";

    // Making sure it's always day
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {

        if(event.getPlugin() == OACore.getPlugin()) {

            final World world = Bukkit.getWorld(OACore.getWorldName());

            resetWorld(world);

            Bukkit.getScheduler().scheduleSyncRepeatingTask(OACore.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    resetWorld(world);
                }
            }, 0L, 5 * 20L);

            OACore.getConsole().sendMessage(messagePrefix + "Protection enabled!");

        }

    }
    public void resetWorld(World world) {
        world.setTime(5900L);
        world.setStorm(false);
        world.setThundering(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginDisableEvent event) {

        if(event.getPlugin() == OACore.getPlugin()) {

            OACore.getConsole().sendMessage(messagePrefix + "Protection disabled!");

        }

    }

    // AntiPvP and AntiBuild are handled in OAGames class

    // Food level
    // TODO: Parkour Low Food
    @EventHandler(priority = EventPriority.LOWEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {

        if(event.getEntity() instanceof Player)
            ((Player) event.getEntity()).setFoodLevel(20);

        event.setCancelled(true);

    }

    // Setting game spawn
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        event.setRespawnLocation(OACore.getGameSpawn());

    }

    public static String getPrimaryColor() {
        return primaryColor;
    }

    public static String getPermPrefix() {
        return permPrefix;
    }

    public static String getMessagePrefix() {
        return messagePrefix;
    }
}
