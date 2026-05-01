package org.psyco.essentialSmp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.psyco.essentialSmp.EssentialSmp;
import org.psyco.essentialSmp.gui.RTPGui;

public class RtpCommand implements CommandExecutor {

    private final EssentialSmp plugin;

    public RtpCommand(EssentialSmp plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!(sender instanceof Player player)) return false;


        RTPGui gui = new RTPGui(plugin);

        gui.onOpen(player);





        return true;
    }
}
