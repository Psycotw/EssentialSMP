package org.psyco.essentialSmp.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.psyco.essentialSmp.EssentialSmp;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class RtpListener implements Listener {

    private final Map<UUID,Long> cooldown = new HashMap<>();
    private final int COOLDOWN = 60;

    private final EssentialSmp plugin;

    public RtpListener(EssentialSmp plugin) {
        this.plugin = plugin;
    }


        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {

            if(!(e.getWhoClicked() instanceof Player player)) return;
            if(e.getClickedInventory() == null) return;
            if(e.getView().getTitle().equalsIgnoreCase("RTP")){

                ItemStack clicked = e.getCurrentItem();

                if(clicked == null || clicked.getType() == Material.AIR) return;

                e.setCancelled(true);

                UUID uuid = player.getUniqueId();
                long now = System.currentTimeMillis();

                if(cooldown.containsKey(uuid)){

                    long nextUse = cooldown.get(uuid);

                    if(now<nextUse){
                        long remaining = (nextUse - now)/1000;
                        Map<String,String> ph = Map.of("seconds",String.valueOf(remaining));

                        player.sendMessage(plugin.getMessage("rtp.cooldown", ph));

                    return;
                }
            }

            cooldown.put(uuid,now +COOLDOWN *1000);

            if(clicked.getType() == Material.GRASS_BLOCK &&
                    clicked.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getMessage("gui.over_world_display"))){

                e.setCancelled(true);

                synchronized (plugin.teleportInProgress) {

                    if (plugin.teleportInProgress.containsKey(player.getUniqueId())) {

                        String type = plugin.teleportInProgress.get(player.getUniqueId());
                        player.playSound(player, Sound.BLOCK_ANVIL_DESTROY, 1f,1f);

                        Map<String,String> ph = Map.of("type",String.valueOf(type));
                        player.sendMessage(plugin.getMessage("rtp.already_teleport",ph));

                        return;
                    }
                }

                plugin.teleportInProgress.put(player.getUniqueId(), "RTP");
                World world = Bukkit.getWorld("world");
                new BukkitRunnable() {

                    int timer = 5;

                    Location initLoc = player.getLocation();

                    public void run() {



                        if(initLoc.distance(player.getLocation()) > 0.5){
                            plugin.teleportInProgress.remove(player.getUniqueId());
                            player.playSound(player, Sound.BLOCK_ANVIL_DESTROY, 1f,1f);
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                    new TextComponent(plugin.getMessage("rtp.cancel_move")));
                            cooldown.remove(player.getUniqueId());
                            cancel();

                            return;

                        }
                        if(!player.isOnline()){
                            cancel();
                        }
                        if(!plugin.teleportInProgress.containsKey(player.getUniqueId())){
                            cancel();
                            return;

                        }



                        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP,1f,1f);

                        Map<String,String> ph = Map.of("seconds",String.valueOf(timer));
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(plugin.getMessage("rtp.start",ph)));
                        timer--;


                        if(timer <= 0){
                            Location safeLoc = getSafelocation(world);

                            player.teleport(safeLoc);
                            player.playSound(player, Sound.ENTITY_PLAYER_TELEPORT, 1f,1f);
                            plugin.teleportInProgress.remove(player.getUniqueId());
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(plugin.getMessage("rtp.success_overworld")));
                            cancel();

                        }





                    }
                }.runTaskTimer(plugin,0,20l);






            } else if(clicked.getType() == Material.NETHERRACK &&
                    clicked.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getMessage("gui.nether_display"))){

                e.setCancelled(true);

                synchronized (plugin.teleportInProgress) {

                    if (plugin.teleportInProgress.containsKey(player.getUniqueId())) {

                        String type = plugin.teleportInProgress.get(player.getUniqueId());
                        player.playSound(player, Sound.BLOCK_ANVIL_DESTROY, 1f,1f);

                        Map<String,String> ph = Map.of("type",String.valueOf(type));
                        player.sendMessage(plugin.getMessage("rtp.already_teleport",ph));

                        return;
                    }
                }

                plugin.teleportInProgress.put(player.getUniqueId(), "RTP");
                World world = Bukkit.getWorld("world_nether");
                new BukkitRunnable() {

                    int timer = 5;

                    Location initLoc = player.getLocation();

                    public void run() {



                        if(initLoc.distance(player.getLocation()) > 0.5){
                            plugin.teleportInProgress.remove(player.getUniqueId());
                            player.playSound(player, Sound.BLOCK_ANVIL_DESTROY, 1f,1f);
                            cooldown.remove(player.getUniqueId());
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                    new TextComponent(plugin.getMessage("rtp.cancel_move")));
                            cancel();

                            return;

                        }
                        if(!player.isOnline()){
                            cancel();
                        }
                        if(!plugin.teleportInProgress.containsKey(player.getUniqueId())){
                            cancel();
                            return;

                        }



                        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP,1f,1f);

                        Map<String,String> ph = Map.of("seconds",String.valueOf(timer));
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(plugin.getMessage("rtp.start",ph)));
                        timer--;
                        if(timer <= 0){

                            Location safeLoc = getNetherSafelocation(world);

                            player.teleport(safeLoc);
                            player.playSound(player, Sound.ENTITY_PLAYER_TELEPORT, 1f,1f);
                            plugin.teleportInProgress.remove(player.getUniqueId());
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(plugin.getMessage("rtp.success_nether")));
                            cancel();

                        }





                    }
                }.runTaskTimer(plugin,0,20l);



            } else if (clicked.getType() == Material.END_STONE &&
            clicked.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getMessage("gui.end_display"))){

                e.setCancelled(true);

                synchronized (plugin.teleportInProgress) {

                    if (plugin.teleportInProgress.containsKey(player.getUniqueId())) {

                        String type = plugin.teleportInProgress.get(player.getUniqueId());
                        player.playSound(player, Sound.BLOCK_ANVIL_DESTROY, 1f,1f);

                        Map<String,String> ph = Map.of("type",String.valueOf(type));
                        player.sendMessage(plugin.getMessage("rtp.already_teleport",ph));

                        return;
                    }
                }
                World world = Bukkit.getWorld("world_the_end");
                plugin.teleportInProgress.put(player.getUniqueId(), "RTP");

                new BukkitRunnable() {

                    int timer = 5;

                    Location initLoc = player.getLocation();

                    public void run() {



                        if(initLoc.distance(player.getLocation()) > 0.5){
                            plugin.teleportInProgress.remove(player.getUniqueId());
                            player.playSound(player, Sound.BLOCK_ANVIL_DESTROY, 1f,1f);
                            cooldown.remove(player.getUniqueId());
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                    new TextComponent(plugin.getMessage("rtp.cancel_move")));
                            cancel();

                            return;

                        }
                        if(!player.isOnline()){
                            cancel();
                        }
                        if(!plugin.teleportInProgress.containsKey(player.getUniqueId())){
                            cancel();
                            return;

                        }


                        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP,1f,1f);

                        Map<String,String> ph = Map.of("seconds",String.valueOf(timer));
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(plugin.getMessage("rtp.start",ph)));

                        timer--;
                        if(timer <= 0){
                            Location safeLoc = getEndSafelocation(world);

                            player.teleport(safeLoc);
                            player.playSound(player, Sound.ENTITY_PLAYER_TELEPORT, 1f,1f);
                            plugin.teleportInProgress.remove(player.getUniqueId());
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(plugin.getMessage("rtp.success_end")));
                            cancel();

                        }





                    }
                }.runTaskTimer(plugin,0,20l);



            }


        }

    }


    private Location getSafelocation(World world) {
        Random random = new Random();


        int  radius = random.nextInt(60000 - 3000) + 3000;

        for(int i = 0; i< 10 ; i++){
            int x = random.nextInt(radius *2) - radius;
            int z = random.nextInt(radius *2 ) - radius ;
            int y = world.getHighestBlockYAt(x, z);

            Location location = new Location(world, x + 0.5, y , z + 0.5);
            Block blockBelow = location.clone().subtract(0, 1, 0).getBlock();
            Material type = blockBelow.getType();
            if (type != Material.WATER && type != Material.KELP && type != Material.LAVA && type != Material.SEAGRASS && type != Material.BUBBLE_CORAL) {

                return location;
            }
        }

        return null;

    }
    private Location getNetherSafelocation(World world) {
        if(world == null) return null;

        Random random = new Random();
        int radius = random.nextInt(60000 -3000) + 3000;
        for(int i = 0; i< 30 ; i++){

            int x = random.nextInt(radius * 2) - radius;
            int z = random.nextInt(radius * 2) - radius;

            for(int y = 100 ; y > 10 ; y--){
                Location location = new Location(world, x + 0.5, y , z + 0.5);
                Block block = location.getBlock();
                Block below = block.getRelative(0,-1,0);
                Block above = block.getRelative(0,1,0);
                if (block.getType() == Material.AIR && below.getType().isSolid() && above.getType() == Material.AIR
                        && block.getType() != Material.LAVA && below.getType() != Material.LAVA && above.getType() != Material.LAVA ) {

                    return location;
                }



            }
        }

        return null;

    }
    private Location getEndSafelocation(World world) {
        Random random = new Random();
        int radius = random.nextInt(3000 - 1000) + 1000;
        for(int i = 0; i< 15 ; i++){

            int x = random.nextInt(radius * 2) - radius;
            int z = random.nextInt(radius * 2) - radius;

            for(int y = 100; y> 10; y--){
                Location location = new Location(world,x +0.5,y,z+0.5);
                Block block = location.getBlock();
                Block below = block.getRelative(0,-1,0);
                Block above = block.getRelative(0,1,0);

                if(block.getType() == Material.AIR && below.getType() == Material.END_STONE && above.getType() == Material.AIR){

                    return location;
                }
            }



        }

        return null;

    }



}
