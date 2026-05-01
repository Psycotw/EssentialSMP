package org.psyco.essentialSmp.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.psyco.essentialSmp.EssentialSmp;

public class DeleteHomeGui {

    private final EssentialSmp plugin;

    public DeleteHomeGui(EssentialSmp plugin) {
        this.plugin = plugin;
    }


    public void deleteHomeGui(Player player) {

        Inventory inv = Bukkit.createInventory(null, 9, plugin.getMessage("gui.delete_home_gui_title"));

        ItemStack Red = new ItemStack(Material.RED_CONCRETE);
        ItemStack Green = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta RedMeta = Red.getItemMeta();
        ItemMeta GreenMeta = Green.getItemMeta();
        RedMeta.setDisplayName(plugin.getMessage("gui.come_back_delete"));
        GreenMeta.setDisplayName(plugin.getMessage("gui.delete_button"));
        Red.setItemMeta(RedMeta);
        Green.setItemMeta(GreenMeta);

        inv.setItem(2,Red);

        inv.setItem(6,Green);

        player.openInventory(inv);
    }
}
