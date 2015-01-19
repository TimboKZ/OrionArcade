package kz.timbo.OrionArcade.Handlers;

import kz.timbo.OrionArcade.Games.Interfaces.OAPvP;
import kz.timbo.OrionArcade.Games.OADeathmatch;
import kz.timbo.OrionArcade.Games.OAGame;
import kz.timbo.OrionArcade.Games.OAParkour;
import kz.timbo.OrionArcade.OACore;
import kz.timbo.OrionArcade.OAUtil;
import kz.timbo.OrionArcade.Objects.OAGameType;
import kz.timbo.OrionArcade.Objects.OAPlayer;
import kz.timbo.OrionArcade.Games.Interfaces.OARespawnable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.HashMap;

public class OAGames implements Listener, CommandExecutor {

    private static String primaryColor = ChatColor.YELLOW + "";
    private static String messagePrefix = OACore.generatePrefix(primaryColor + ChatColor.ITALIC + "Games");

    private static String permPrefix = OACore.getPermPrefix() + "game.";

    private static HashMap<Location, OAGame> signLocations = new HashMap<Location, OAGame>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {

        if(event.getPlugin() == OACore.getPlugin()) {

            World world = OACore.getWorld();

            /*
             * Deathmatch
             */
            signLocations.put(
                    new Location(world, -304, 37, 944),
                    new OADeathmatch(
                            1,
                            "Nether",
                            16,
                            new Location(world, -304, 37, 944),
                            new Location[]{
                                    new Location(world, -334, 35, 931),
                                    new Location(world, -303, 50, 900)
                            },
                            new Location[]{
                                    new Location(world, -304.4, 42.0, 929.5, 139.3f, 11.3f),
                                    new Location(world, -319.4, 41.0, 928.4, 180.3f, 16.7f),
                                    new Location(world, -329.5, 41.0, 916.5, -88.2f, 8.6f),
                                    new Location(world, -329.5, 40.0, 906.5, -56.9f, 19.5f),
                                    new Location(world, -314.4, 43.0, 902.8, -0.3f, 23.8f),
                                    new Location(world, -304.9, 42.0, 902.8, 46.5f, 31.7f),
                                    new Location(world, -308.3, 42.0, 911.7, 62.7f, 27.6f)
            }
                    )
            );

            /*
             * Parkour
             */
            signLocations.put(
                    new Location(world, -359, 37, 999),
                    new OAParkour(
                            1,
                            "Run!",
                            24,
                            new Location(world, -359, 37, 999),
                            new Location[]{
                                    new Location(world, -360, 35, 1016),
                                    new Location(world, -409, 50, 1001)
                            },
                            "Easy",
                            new Location(world, -361.6, 38.0, 1014.5, 90.0f, -0.1f)
                    )
            );

            OACore.getConsole().sendMessage(messagePrefix + "Games enabled!");

        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {

        if(event.getPlugin() == OACore.getPlugin()) {

            for(OAGame game : signLocations.values())
                game.stopGame("reload");

            signLocations.clear();

            OACore.getConsole().sendMessage(messagePrefix + "Games disabled!");

        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        OAPlayer player = OACore.getPlayerManager().getPlayer(event.getPlayer());

        if(player.isInGame())
            player.getCurrentGame().abandonGame(player);

    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            OACore.getConsole().sendMessage(messagePrefix + "Sorry, only players can use game-related commands");
            return true;
        }

        OAPlayer player = OACore.getPlayerManager().getPlayer((Player) sender);

        for(OAGameType gameType : OAGameType.values())
            if(command.getName().equalsIgnoreCase(gameType.getCommandName()))
                enterGameSpawn(player, gameType);

        return true;

    }

    public static void enterGameSpawn(OAPlayer player, OAGameType gameType) {
        if(player.getPermUser().has(gameType.getPerm())) {
            player.getPlayer().teleport(gameType.getHubLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
            player.sendMessage(gameType.getMessagePrefix() + "Teleporting to game hub...");
        } else {
            player.sendMessage(gameType.getMessagePrefix() + "You are not allowed to play this game.");
            player.playSound("notAllowed");
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

        OAPlayer player = OACore.getPlayerManager().getPlayer(event.getPlayer());

        String command = event.getMessage().split(" ")[0];

        if(player.isInGame()) {

            OAGame game = player.getCurrentGame();
            OAGameType gameType = game.getType();

            if(command.equalsIgnoreCase("/leave"))
                game.leaveGame(player);
            else {
                player.sendMessage(gameType.getMessagePrefix() + "You can only use " + gameType.getColor() + "/leave" + ChatColor.RESET + " while in a game.");
                player.playSound("notAllowed");
            }


            event.setCancelled(true);

        } else if(player.isInShop()) {



        } else if(command.equalsIgnoreCase("/leave")) {

            player.sendMessage(messagePrefix + "There's nothin' to leave :)");
            player.playSound("notifyBad");

        }



    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Block block = event.getClickedBlock();

        if(!OAUtil.isSign(block))
            return;

        OAGame game = signLocations.get(block.getLocation());

        OAPlayer player = OACore.getPlayerManager().getPlayer(event.getPlayer());

        if(game == null) {

            if(player.isInGame()) {

                game = player.getCurrentGame();

                if(game instanceof OAParkour) {

                    Sign sign = (Sign) block.getState();

                    if(ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[Checkpoint]")) {

                        ((OAParkour) game).setCheckpoint(player, block);

                    }

                }

            }

            return;
        }

        game.joinGame(player);

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        OAPlayer player = OACore.getPlayerManager().getPlayer(event.getPlayer());

        if(player.isInGame()) {

            OAGame game = player.getCurrentGame();

            if(game instanceof OAParkour) {

                if(event.getTo().getBlock().getType() == Material.STATIONARY_WATER || event.getTo().getBlock().getType() == Material.WATER) {
                    ((OAParkour) game).reset(player);
                }

            }

        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityShootBow(EntityShootBowEvent event) {

        if(!(event.getEntity() instanceof Player))
            return;

        OAPlayer player = OACore.getPlayerManager().getPlayer((Player) event.getEntity());

        if(player.isInGame()) {

            OAGame game = player.getCurrentGame();

            if(game instanceof OAPvP) {

                if(!((OAPvP) game).isBowAllowed()) {

                    player.sendMessage(player.getCurrentGame().getType().getMessagePrefix() + "You can't shoot a bow in this game!");
                    player.playSound("notAllowed");
                    event.setCancelled(true);

                }


            } else {

                player.sendMessage(player.getCurrentGame().getType().getMessagePrefix() + "You can't shoot a bow in this game!");
                player.playSound("notAllowed");
                event.setCancelled(true);

            }

        } else {

            if(!player.getPermUser().has(OACore.getPermPrefix() + "pvp.bow")) {

                player.sendMessage(OAProtect.getMessagePrefix() + "Nope, you are not allowed to shoot a bow.");
                player.playSound("notAllowed");
                event.setCancelled(true);

            }

        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        Entity damagerEntity = event.getDamager();
        Entity victimEntity = event.getEntity();

        if(!(victimEntity instanceof Player))
            return;

        OAPlayer victim = OACore.getPlayerManager().getPlayer((Player) victimEntity);

        if(!(damagerEntity instanceof Player)) {

            if(victim.isInGame()) {

                OAGame game = victim.getCurrentGame();
                boolean cancel = true;

                if(game instanceof OAPvP)
                    if (damagerEntity instanceof Arrow)
                        if (((OAPvP) game).isBowAllowed())
                            cancel = false;

                // TODO: Add checks for Mob Arena

                event.setCancelled(cancel);
                return;

            }

            event.setCancelled(true);
            return;

        }

        OAPlayer damager = OACore.getPlayerManager().getPlayer((Player) damagerEntity);

        if(damager.isInGame() && victim.isInGame()) {

            if(damager.getCurrentGame() == victim.getCurrentGame()) {

                OAGame game = damager.getCurrentGame();

                if(game instanceof OAPvP) {

                    boolean cancel = true;



                    if(event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                        if(((OAPvP) game).isBowAllowed())
                            cancel = false;
                        else
                            cancel = true;
                    } else if(((OAPvP) game).isMeleeAllowed())
                        cancel = false;

                    event.setCancelled(cancel);

                }

                return;

            }

            damager.sendMessage(messagePrefix + "You should be in a different game!");
            damager.playSound("notAllowed");
            event.setCancelled(true);

            return;

        }

        if(damager.getPermUser().has(OACore.getPermPrefix() + "pvp.attack")) {
            if(victim.getPermUser().has(OACore.getPermPrefix() + "pvp.ignore")) {
                damager.sendMessage(OAProtect.getMessagePrefix() + "Nice try, but this player is protected :D");
                victim.sendMessage(OAProtect.getMessagePrefix() + damager.getShortDisplayName() + " tried to attack you but you were blessed!");
                event.setCancelled(true);
            }
        } else {
            damager.sendMessage(OAProtect.getMessagePrefix() + "C'mon, you can't just randomly hit people!");
            damager.playSound("notAllowed");
            event.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {

        OAPlayer player = OACore.getPlayerManager().getPlayer(event.getPlayer());

        if(player.isInGame()) {
            event.setCancelled(true);
            player.sendMessage(player.getCurrentGame().getType().getMessagePrefix() + "You can't place blocks in this game!");
            player.playSound("notAllowed");
        } else if(player.isInShop()) {
            // TODO: Do something.
        } else if(!player.getPermUser().has(OACore.getPermPrefix() + "build.place")) {
            event.setCancelled(true);
            player.sendMessage(OAProtect.getMessagePrefix() + "Sorry Bro, it's an " + OACore.getArcade() + ChatColor.RESET + " server!");
            player.playSound("notAllowed");
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {

        OAPlayer player = OACore.getPlayerManager().getPlayer(event.getPlayer());

        if(player.isInGame()) {
            event.setCancelled(true);
            player.sendMessage(player.getCurrentGame().getType().getMessagePrefix() + "You can't break blocks in this game!");
            player.playSound("notAllowed");
        } else if(player.isInShop()) {
            // TODO: Do something.
        } else if(!player.getPermUser().has(OACore.getPermPrefix() + "build.break")) {
            event.setCancelled(true);
            player.sendMessage(OAProtect.getMessagePrefix() + "Sorry Bro, it's an " + OACore.getArcade() + ChatColor.RESET + " server!");
            player.playSound("notAllowed");
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {

        OAPlayer victim = OACore.getPlayerManager().getPlayer(event.getEntity());

        if(victim.isInGame()) {

            OAGame game = victim.getCurrentGame();

            if(game instanceof  OAPvP) {

                ((OAPvP) game).addDeaths(victim);

                Player killer = victim.getPlayer().getKiller();

                if(killer != null) {

                    OAPlayer killerPlayer = OACore.getPlayerManager().getPlayer(killer);

                    if(killerPlayer.isInGame() && killerPlayer.getCurrentGame() == game) {

                        ((OAPvP) game).addKills(killerPlayer);
                        killerPlayer.playSound("notifyGood");

                    }

                }

            }

        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        OAPlayer player = OACore.getPlayerManager().getPlayer(event.getPlayer());

        if(player.isInGame()) {
            OAGame game = player.getCurrentGame();
            if(game instanceof OARespawnable)
                event.setRespawnLocation(((OARespawnable) game).getSpawnPoint(player));
        }

    }

    public static String getPrimaryColor() {
        return primaryColor;
    }

    public static String getMessagePrefix() {
        return messagePrefix;
    }

    public static String getPermPrefix() {
        return permPrefix;
    }

}
