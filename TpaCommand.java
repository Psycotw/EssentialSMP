package org.psyco.essentialSmp.commands;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.psyco.essentialSmp.EssentialSmp;
import org.psyco.essentialSmp.listeners.ListHomeEvents;
import org.psyco.essentialSmp.manager.TpaManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaCommand implements CommandExecutor {

    private final Map<UUID, UUID> tpaRequests;
    private final EssentialSmp plugin;
    public final Map<UUID, String> teleportInProgress;



    public TpaCommand(Map<UUID, UUID> tpaRequests, EssentialSmp plugin, Map<UUID, String> teleportInProgress) {
        this.tpaRequests = tpaRequests;
        this.plugin = plugin;
        this.teleportInProgress = teleportInProgress;

    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


        if(!(sender instanceof Player))return false;

        Player player = (Player) sender;


        if(command.getName().equalsIgnoreCase("tpa")) {

            return handleTpa(player, args);

        } else if(command.getName().equalsIgnoreCase("tpaccept")) {
            return handleTpaAccept(player);

        }else if(command.getName().equalsIgnoreCase("tpdeny")) {
            return handleTpaDeny(player);

        }

        return true;
    }


    private boolean handleTpa(Player sender, String[] args) {

        if(args.length != 1) {
            sender.sendMessage(plugin.getMessage("tpa.tpa_message"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) {
            sender.sendMessage(plugin.getMessage("freeze.player_not_found"));
            return true;
        }

        if(target.getUniqueId().equals(sender.getUniqueId())) {

            sender.sendMessage(plugin.getMessage("tpa.self_tpa"));
            return true;

        }

        tpaRequests.put(target.getUniqueId(), sender.getUniqueId());

        TpaManager tpaManager = new TpaManager(tpaRequests);
        tpaManager.sendTpaRequest(sender, target);

        return true;
    }
    private boolean handleTpaAccept(Player target) {

        UUID requesterUUID = tpaRequests.get(target.getUniqueId());

        if (requesterUUID == null) {

            target.sendMessage(plugin.getMessage("tpa.request_not_found"));

            return true;
        }


        Player requester = Bukkit.getPlayer(requesterUUID);

        synchronized (plugin.teleportInProgress) {
            if (plugin.teleportInProgress.containsKey(requester.getUniqueId())) {
                String type = plugin.teleportInProgress.get(requester.getUniqueId());
                requester.sendMessage(plugin.getMessage("rtp.already_teleport",Map.of("type", type)));
                return true;
            }


            plugin.teleportInProgress.put(requester.getUniqueId(), "TPA");

            Location starter = target.getLocation();


            new BukkitRunnable() {

                int timer = 10;

                @Override
                public void run() {


                    if (target.getLocation().distance(starter) > 0.5) {
                        target.sendMessage(plugin.getMessage("tpa.move_tpa_error"));
                        tpaRequests.remove(target.getUniqueId());

                        if (target == null) target.sendMessage(plugin.getMessage("tpa.tpa_error"));
                        tpaRequests.remove(target.getUniqueId());
                        plugin.teleportInProgress.remove(target.getUniqueId());
                        cancel();
                        return;

                    }



                    requester.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new TextComponent(plugin.getMessage("tpa.requester_teleport_msg",Map.of("timer",String.valueOf(timer)))));

                    timer--;

                    if (timer <= 0) {

                        if (requester != null) {
                            requester.teleport(target.getLocation());

                            requester.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(plugin.getMessage("tpa.success_tpa")));
                            tpaRequests.remove(requester.getUniqueId());

                            plugin.teleportInProgress.remove(requester.getUniqueId());
                            cancel();
                        }
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L);


            return true;
        }
    }

    private boolean handleTpaDeny(Player target){

        UUID requesterUUID = tpaRequests.get(target.getUniqueId());
        if(requesterUUID == null) {
            target.sendMessage(plugin.getMessage("tpa.request_not_found"));

            return true;
        }

        Player requester = Bukkit.getPlayer(requesterUUID);
        tpaRequests.remove(target.getUniqueId());
        if(requester != null) {
            requester.sendMessage(plugin.getMessage("tpa.rejected_tpa",Map.of("target",target.getName())));
            return true;
        }
        target.sendMessage(plugin.getMessage("tpa.reject_tpa"));




        return true;

    }

}
