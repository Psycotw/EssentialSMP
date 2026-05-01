package org.psyco.essentialSmp.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.psyco.essentialSmp.EssentialSmp;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FreezeCommand implements CommandExecutor , Listener {

    private final EssentialSmp plugin;


    private final Set<UUID> playerFreeze = new HashSet<>();

    public FreezeCommand(EssentialSmp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!(sender instanceof Player)) return false;


        Player player = (Player) sender;

        if(args.length == 0){

            player.sendMessage(plugin.getMessage("freeze.command_freeze"));
            return true;
        }


        Player target = Bukkit.getPlayer(args[0]);

        if(target == null){
            player.sendMessage(plugin.getMessage("freeze.player_not_found"));

        return true;
        }



        if(playerFreeze.contains(target.getUniqueId())){

            playerFreeze.remove(target.getUniqueId());
            player.sendMessage(plugin.getMessage("freeze.freeze_off",Map.of("player",target.getName())));



        } else {
            playerFreeze.add(target.getUniqueId());
            player.sendMessage(plugin.getMessage("freeze.freeze_on", Map.of("player", target.getName())));


        }
         return true;
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){

        Player player = e.getPlayer();
        if(playerFreeze.contains(player.getUniqueId())){
        Location from = e.getFrom();


        e.setTo(from);
        }
    }
}
