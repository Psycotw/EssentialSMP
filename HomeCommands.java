package org.psyco.essentialSmp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.psyco.essentialSmp.EssentialSmp;
import org.psyco.essentialSmp.gui.ListHomeGui;
import org.psyco.essentialSmp.listeners.ListHomeEvents;
import org.psyco.essentialSmp.manager.HomeManager;

import java.util.List;
import java.util.Map;

public class HomeCommands implements CommandExecutor {

    private final HomeManager homes;
    private final EssentialSmp plugin;


    public HomeCommands(HomeManager homes, EssentialSmp plugin) {
        this.homes = homes;
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)) return false;

        String cmd = command.getName().toLowerCase();

        if(cmd.equalsIgnoreCase("sethome")) {

            if(args.length < 1){
                player.sendMessage(plugin.getMessage("home.set_home"));

                return true;
            }
            String name = args[0];

            if(homes.homeExists(player.getUniqueId(), name)){

                player.sendMessage(plugin.getMessage("home.already_exist",Map.of("home", name)));

                return true;

            }

            if(name != null && !name.isEmpty()){


                boolean ok = homes.setHome(player.getUniqueId(),name,player.getLocation());



                if(!ok){

                    int max = homes.getMaxHomes(player.getUniqueId());



                    player.sendMessage(plugin.getMessage("home.max_home",Map.of("max",""+ max)));

                } else {

                    homes.setHome(player.getUniqueId(), name, player.getLocation());



                    player.sendMessage(plugin.getMessage("home.set_home_msg",Map.of("name", name)));
                }

            }

        } else if(cmd.equalsIgnoreCase("home")) {

            ListHomeGui listHomeGui = new ListHomeGui(homes,plugin);
            listHomeGui.openHomeGUI(player);


        } else if(cmd.equalsIgnoreCase("delhome")){

                if(args.length < 1){

                    player.sendMessage(plugin.getMessage("home.del_home"));
                    return true;
                }
                String HomeName = args[0];

                if(HomeName == null || HomeName.isEmpty())return false;


                if(!homes.homeExists(player.getUniqueId(),HomeName)){

                    player.sendMessage(plugin.getMessage("home.null_home_selected"));
                    return true;
                } else {


                    homes.deleteHome(player.getUniqueId(), HomeName);

                    player.sendMessage(plugin.getMessage("home.home_deleted", Map.of("name", HomeName)));

                }
        }

        return true;
    }
}
