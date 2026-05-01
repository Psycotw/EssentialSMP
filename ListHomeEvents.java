package org.psyco.essentialSmp.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.psyco.essentialSmp.EssentialSmp;
import org.psyco.essentialSmp.gui.DeleteHomeGui;
import org.psyco.essentialSmp.gui.ListHomeGui;
import org.psyco.essentialSmp.manager.HomeManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ListHomeEvents implements Listener {

    private final HomeManager hm;
    private final EssentialSmp instance;
    private final DeleteHomeGui deleteHomeGui;
    private final Map<UUID, String> teleportInProgress;

    private final ListHomeGui listHomeGui;

    private final Map<UUID, String> pendingDelete = new HashMap<>();




    public ListHomeEvents(HomeManager hm, EssentialSmp instance, DeleteHomeGui deleteHomeGui, Map<UUID, String> teleportInProgress, ListHomeGui listHomeGui) {
        this.hm = hm;
        this.instance = instance;
        this.deleteHomeGui = deleteHomeGui;
        this.teleportInProgress = teleportInProgress;
        this.listHomeGui = listHomeGui;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {


        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getClickedInventory() == null) return;
        if (e.getView().getTitle().equalsIgnoreCase(instance.getMessage("gui.homes_title",Map.of("player", player.getName())))){



            e.setCancelled(true);

            if (e.getClick() == ClickType.LEFT) {



            ItemStack clicked = e.getCurrentItem();

            if (clicked == null || clicked.getType() == Material.AIR) return;

            String homeName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

            if (hm.homeExists(player.getUniqueId(), homeName)) {
                synchronized (instance.teleportInProgress) {
                    if (instance.teleportInProgress.containsKey(player.getUniqueId())) {
                        String type = instance.teleportInProgress.get(player.getUniqueId());
                        player.sendMessage(instance.getMessage("rtp.already_teleport",Map.of("type", type)));
                        return;
                    }

                    instance.teleportInProgress.put(player.getUniqueId(), "HOME");

                    Location starter = player.getLocation();

                    new BukkitRunnable() {
                        int timer = 5;

                        public void run() {


                            if (!player.isOnline() || player.getLocation().distance(starter) > 0.5) {

                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(instance.getMessage("rtp.cancel_move")));
                                instance.teleportInProgress.remove(player.getUniqueId());
                                player.playSound(player, Sound.BLOCK_ANVIL_DESTROY, 1f,1f);
                                cancel();
                                return;
                            }
                            if (!instance.teleportInProgress.containsKey(player.getUniqueId())) {
                                player.playSound(player, Sound.BLOCK_ANVIL_DESTROY, 1f,1f);
                                cancel();
                                return;
                            }

                            player.playSound(player,Sound.ENTITY_PLAYER_LEVELUP,1f,1f);

                            Map<String,String> ph = Map.of("timer",String.valueOf(timer));

                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(instance.getMessage("home.home_teleport",ph)));
                            timer--;


                            if (timer <= 0) {

                                player.playSound(player, Sound.ENTITY_PLAYER_TELEPORT, 1f,1f);
                                player.teleport(hm.getHome(player.getUniqueId(), homeName));

                                Map<String,String> ph2 = Map.of("home",homeName);

                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(instance.getMessage("home.teleported_home",ph2)));
                                player.closeInventory();

                                instance.teleportInProgress.remove(player.getUniqueId());

                                cancel();
                            }

                        }

                    }.runTaskTimer(instance, 0, 20);


                }
            }


            } else if (e.getClick() == ClickType.RIGHT) {


                ItemStack clicked = e.getCurrentItem();

                if (clicked == null || clicked.getType() == Material.AIR) return;


                if(clicked.getType() == Material.RED_BED){

                    String homeName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

                    pendingDelete.put(player.getUniqueId(), homeName);

                    player.closeInventory();

                    deleteHomeGui.deleteHomeGui(player);

                }

            }


        } else if(e.getView().getTitle().equalsIgnoreCase(instance.getMessage("gui.delete_home_gui_title"))) {

            e.setCancelled(true);

            ItemStack clicked = e.getCurrentItem();

            if (clicked == null || clicked.getType() == Material.AIR) return;

            if(clicked.getType() == Material.RED_CONCRETE) {

                e.setCancelled(true);

                pendingDelete.remove(player.getUniqueId());

                player.closeInventory();
                listHomeGui.openHomeGUI(player);

            } else if(clicked.getType() == Material.GREEN_CONCRETE) {

                e.setCancelled(true);


                String homeName = pendingDelete.get(player.getUniqueId());

                if (homeName == null) {
                    player.sendMessage(instance.getMessage("home.null_home_selected"));
                    return;
                }

                hm.deleteHome(player.getUniqueId(), homeName);
                pendingDelete.remove(player.getUniqueId());

                player.sendMessage(instance.getMessage("home.home_deleted",Map.of("name",homeName)));
                e.getInventory().remove(clicked);


                player.updateInventory();
                player.closeInventory();

                listHomeGui.openHomeGUI(player);
            }



        }
    }
}
