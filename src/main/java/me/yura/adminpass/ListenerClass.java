package me.yura.adminpass;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static me.yura.adminpass.AdminPass.*;

public class ListenerClass implements Listener {

    @EventHandler
    public void onAdminJoinEvent(PlayerJoinEvent e) {
        //Check player
        if (enableAdminPass && (groupsPasswords.containsKey(getPermissions().getPlayerGroups(e.getPlayer())[0]))) {
            //Add to queue
            inQueue.add(e.getPlayer().getName());

            //Deny pickup items
            e.getPlayer().setCanPickupItems(false);

            //Send message
            e.getPlayer().sendMessage("§cВведите админ пароль! §bИспользование /apass пароль");

            //Start timer
            new BukkitRunnable() {
                public void run() {
                    if (inQueue.contains(e.getPlayer().getName())) {
                        e.getPlayer().kickPlayer("§cПароль не введён!");
                        inQueue.remove(e.getPlayer().getName());
                    }
                }
            }.runTaskLater(getInstance(), 1200L);
        }
    }


    /*
    Cancel moving
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (inQueue.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
        }
    }

    /*
    Cancel commands execution
     */
    @EventHandler
    public void onPlayerCmd(PlayerCommandPreprocessEvent e) {
        if (inQueue.contains(e.getPlayer().getName()) && !e.getMessage().startsWith("/apass")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cЗапрещено вводить команды");
        }
    }

    /*
    Cancel chatting
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (inQueue.contains(e.getPlayer().getName()) && !e.getMessage().startsWith("/apass")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cЗапрещено писать в чат");
        }
    }

    /*
    Cancel tab complete
     */
    @EventHandler
    public void onCmd(TabCompleteEvent e) {
        if (e.getSender() instanceof Player && inQueue.contains((e.getSender()).getName())) {
            e.setCancelled(true);
        }
    }

    /*
    Cancel interacting
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (inQueue.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
        }
    }

    /*
    If player leave - remove from list
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        e.getPlayer().setCanPickupItems(true);
        inQueue.removeIf(user -> user.equals(e.getPlayer().getName()));
    }


    /*
    Cancel drop items
     */
    @EventHandler
    public void itemDrop(PlayerDropItemEvent e) {
        if (inQueue.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
        }
    }

    /*
    Cancel teleporting
     */
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e){
        if(e.getCause().equals(PlayerTeleportEvent.TeleportCause.PLUGIN)) return;

        if (inQueue.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
        }
    }

}
