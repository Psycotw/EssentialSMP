package org.psyco.essentialSmp.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.psyco.essentialSmp.EssentialSmp;
import org.psyco.essentialSmp.manager.HomeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListHomeGui {

    private final HomeManager hm;
    private final EssentialSmp plugin;

    public ListHomeGui(HomeManager hm, EssentialSmp plugin) {
        this.hm = hm;
        this.plugin = plugin;
    }


    public void openHomeGUI(Player player) {

        Map<String, Location> homes = hm.getAllHomes(player.getUniqueId());




        Inventory inv = Bukkit.createInventory(null, 27, plugin.getMessage("gui.homes_title",Map.of("player", player.getName())));

        for (int i = 9; i < 18; i++) {

            ItemStack item = inv.getItem(i);

            if (item == null || item.getType() == Material.AIR) {

                ItemStack gray = new ItemStack(Material.GRAY_BED);
                ItemMeta meta = gray.getItemMeta();
                meta.setDisplayName(plugin.getMessage("gui.free_slot"));
                gray.setItemMeta(meta);

                inv.setItem(i, gray);
            }


        }
        int slot = 9;

        for (String homeName : homes.keySet()) {

            while (slot < 18 && inv.getItem(slot).getType() != Material.GRAY_BED) {
                slot++;
            }

            if(slot >= 18)break;

            ItemStack Bed = new ItemStack(Material.RED_BED);
            ItemMeta meta = Bed.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + homeName);
            List<String> lore = new ArrayList<>();

            lore.add(plugin.getMessage("gui.delete_home_msg"));
            lore.add(plugin.getMessage("gui.tp_home_msg"));
            meta.setLore(lore);
            Bed.setItemMeta(meta);

            inv.setItem(slot,Bed);
            slot++;

        }

        player.openInventory(inv);
    }


}
