package org.psyco.essentialSmp.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.psyco.essentialSmp.EssentialSmp;

import java.util.List;
import java.util.UUID;

public class VanishListener implements Listener {

    private final EssentialSmp plugin;
    private final List<UUID> staffPlayers;

    public VanishListener(EssentialSmp plugin, List<UUID> staffPlayers) {
        this.plugin = plugin;
        this.staffPlayers = staffPlayers;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player joing = e.getPlayer();

        for(UUID uuid : staffPlayers) {

            Player vanished = Bukkit.getPlayer(uuid);
            if(vanished != null) {
                joing.hidePlayer(plugin,vanished);
            }
        }
    }

}
