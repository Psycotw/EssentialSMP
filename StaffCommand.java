package org.psyco.essentialSmp.commands;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.psyco.essentialSmp.EssentialSmp;


import java.util.List;
import java.util.UUID;

public class StaffCommand implements CommandExecutor {

    private final List<UUID> staffPlayers;
    private final EssentialSmp plugin;
    public StaffCommand(List<UUID> staffPlayers, EssentialSmp plugin) {
        this.staffPlayers = staffPlayers;
        this.plugin = plugin;

    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if (player.hasPermission("essentialSmp.staff")) {


            if(args.length == 0) {
                player.sendMessage(plugin.getMessage("staff.command_inv"));
                player.sendMessage(plugin.getMessage("staff.command_vanish"));
                return true;
            }



                if(args[0].equalsIgnoreCase("inv")) {

                    if(args.length < 2){
                        player.sendMessage(plugin.getMessage("staff.command_inv"));
                        return true;
                    }

                    Player target = Bukkit.getPlayer(args[1]);

                    if(target != null) {

                        player.openInventory(target.getInventory());

                    }


                    return true;
                }

                if(args[0].equalsIgnoreCase("vanish")) {


                    if(staffPlayers.contains(player.getUniqueId())) {

                        for(Player people : Bukkit.getOnlinePlayers()) {
                            people.showPlayer(plugin,player);
                        }

                        staffPlayers.remove(player.getUniqueId());
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(plugin.getMessage("staff.out_vanish")));

                        return true;


                    }

                    for (Player people : Bukkit.getOnlinePlayers()) {

                        people.hidePlayer(player);

                    }
                    staffPlayers.add(player.getUniqueId());
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(plugin.getMessage("staff.in_vanish")));

                    return true;
                }


                }

        return true;
    }
}
