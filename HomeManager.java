package org.psyco.essentialSmp.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.psyco.essentialSmp.EssentialSmp;
import org.psyco.essentialSmp.EssentialSmp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeManager {

    private final EssentialSmp plugin;
    private final Map<UUID, Map<String, Location>> homes = new HashMap<>();
    private File dataFile;
    private FileConfiguration dataConfig;


    public HomeManager(EssentialSmp plugin) {
        this.plugin = plugin;

        dataFile = new File(plugin.getDataFolder(), "homes.yml");
        if(!dataFile.exists()) {
            try{
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        loadHomes();
        savesHome();

    }

    private void loadHomes() {
        for (String key : dataConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            Map<String, Location> playerHomes = new HashMap<>();

            if (dataConfig.getConfigurationSection(key) == null) continue;

            for (String homeName : dataConfig.getConfigurationSection(key).getKeys(false)) {
                Location loc = dataConfig.getLocation(key + "." + homeName);
                if (loc != null) {
                    playerHomes.put(homeName.toLowerCase(), loc);
                }
            }

            homes.put(uuid, playerHomes);
        }
    }
    public void savesHome(){
        for (UUID uuid : homes.keySet()) {
            Map<String, Location> playerHomes = homes.get(uuid);
            for (String name : playerHomes.keySet()) {
                dataConfig.set(uuid.toString() + "." + name, playerHomes.get(name));
            }
        }
        try{
            dataConfig.save(dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean setHome(UUID uuid, String name, Location loc) {

        String key = name.toLowerCase();
        Map<String, Location> playerHomes = homes.get(uuid);

        if(playerHomes.containsKey(key)) {
            playerHomes.put(key, loc);
            savesHome();
            return true;
        }
        int max  = getMaxHomes(uuid);
        int current = playerHomes.size();

        if(current >= max) {
            return false;
        }
        playerHomes.put(key, loc);
        savesHome();
        return true;
    }

    public Location getHome(UUID uuid,String name) {

        if(homes.containsKey(uuid)) {

            return homes.get(uuid).get(name);

        }
        return null;

    }

    public void deleteHome(UUID uuid ,String name) {

        if(!homes.containsKey(uuid))return;
        Map<String, Location> playerHomes = homes.get(uuid);

        if(playerHomes.containsKey(name.toLowerCase())) {
            playerHomes.remove(name.toLowerCase());
            dataConfig.set(uuid.toString() + "." + name.toLowerCase(), null);
            try{
                dataConfig.save(dataFile);
            }catch(IOException e){
                e.printStackTrace();
            }
        }

    }
    public boolean homeExists(UUID uuid,String name) {

        return homes.containsKey(uuid) && homes.get(uuid).containsKey(name);

    }

    public Map<String, Location> getAllHomes(UUID uuid) {
        return homes.getOrDefault(uuid, new HashMap<>());

    }
    public int getHomeCount(UUID uuid) {
        return homes.getOrDefault(uuid, new HashMap<>()).size();
    }

    public int getMaxHomes(UUID uuid) {
        if(uuid == null) return 3;

        if(Bukkit.getPlayer(uuid) != null){

            if(Bukkit.getPlayer(uuid).hasPermission("vip.9")) {
                return 9;
            } else if(Bukkit.getPlayer(uuid).hasPermission("vip.5")){
                return 5;
            }
        }
        return 3;
    }
}
