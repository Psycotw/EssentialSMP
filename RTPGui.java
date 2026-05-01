package org.psyco.essentialSmp.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.psyco.essentialSmp.EssentialSmp;

public class RTPGui {


    private final EssentialSmp plugin;

    public RTPGui(EssentialSmp plugin) {
        this.plugin = plugin;
    }


    public void onOpen(Player player) {


        Inventory inv = Bukkit.createInventory(null,27,plugin.getMessage("gui.rtp_title"));


        ItemStack overWorld = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta overWorldMeta = overWorld.getItemMeta();
        overWorldMeta.setDisplayName(plugin.getMessage("gui.over_world_display"));
        overWorld.setItemMeta(overWorldMeta);
        inv.setItem(10, overWorld);

        ItemStack nether = new ItemStack(Material.NETHERRACK);
        ItemMeta netherMeta = nether.getItemMeta();
        netherMeta.setDisplayName(plugin.getMessage("gui.nether_display"));
        nether.setItemMeta(netherMeta);
        inv.setItem(13, nether);

        ItemStack ender = new ItemStack(Material.END_STONE);
        ItemMeta enderMeta = ender.getItemMeta();
        enderMeta.setDisplayName(plugin.getMessage("gui.end_display"));
        ender.setItemMeta(enderMeta);
        inv.setItem(16, ender);


        player.openInventory(inv);
    }

}
