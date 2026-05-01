package org.psyco.essentialSmp;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.psyco.essentialSmp.commands.*;
import org.psyco.essentialSmp.gui.DeleteHomeGui;
import org.psyco.essentialSmp.gui.ListHomeGui;
import org.psyco.essentialSmp.listeners.ListHomeEvents;
import org.psyco.essentialSmp.listeners.RtpListener;
import org.psyco.essentialSmp.listeners.VanishListener;
import org.psyco.essentialSmp.manager.DBManager;
import org.psyco.essentialSmp.manager.HomeManager;
import org.psyco.essentialSmp.manager.TpaManager;

import java.io.File;
import java.util.*;

public final class EssentialSmp extends JavaPlugin {

    public List<UUID> staffPlayers = new ArrayList<>();
    public Map<UUID, UUID> tpaRequests = new HashMap<>();
    public final Map<UUID, String> teleportInProgress = new HashMap<>();

    private DBManager dbManager;
    private  HomeManager homeManager;
    private TpaManager tpaManager;
    private  TpaCommand tpaCommand;

    private ListHomeEvents listHomeEvents;

    private   ListHomeGui listHomeGui;

    private static EssentialSmp instance;

    private DeleteHomeGui deleteHomeGui;
    public FileConfiguration messages;
    public File messagesFile;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        createMessageFile();



        //dbManager = new DBManager(this);
        this.homeManager = new HomeManager(this);
        DeleteHomeGui deleteHomeGui = new DeleteHomeGui(this);
         listHomeGui = new ListHomeGui(homeManager,this);
         tpaCommand = new TpaCommand(tpaRequests, this,teleportInProgress);
         listHomeEvents = new ListHomeEvents(homeManager,this,deleteHomeGui,teleportInProgress,listHomeGui);


        //dbManager.connect();

        //createHomesTable();

        FreezeCommand freezeCommand = new FreezeCommand(this);

        getCommand("ec").setExecutor(new EcCommand());
        getCommand("s").setExecutor(new StaffCommand(staffPlayers, this));
        getServer().getPluginManager().registerEvents(new VanishListener(this,staffPlayers), this);
        getCommand("tpa").setExecutor(tpaCommand);
        getCommand("tpaccept").setExecutor(tpaCommand);
        getCommand("tpdeny").setExecutor(tpaCommand);
        getCommand("freeze").setExecutor(freezeCommand);
        getServer().getPluginManager().registerEvents(freezeCommand, this);
        getCommand("home").setExecutor(new HomeCommands(homeManager,this));
        getCommand("setHome").setExecutor(new HomeCommands(homeManager,this));
        getCommand("delhome").setExecutor(new HomeCommands(homeManager,this));
        getServer().getPluginManager().registerEvents(listHomeEvents, this);
        getCommand("rtp").setExecutor(new RtpCommand(this));
        getServer().getPluginManager().registerEvents(new RtpListener(this),this);

    }
    @Override
    public void onDisable() {
       /*if(dbManager != null) {
            dbManager.disconnect();
        }*/

        if(homeManager != null) {
            homeManager.savesHome();
        }
    }

    private void createMessageFile(){
        messagesFile = new File(getDataFolder(),"messages.yml");
        if(!messagesFile.exists()){
            saveResource("messages.yml",false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String path){
        return ChatColor.translateAlternateColorCodes('&',messages.getString(path,"&c messaggio non trovato"));

    }

    public String getMessage(String path,Map<String,String> placeHolders){

        String msg = getMessage(path);

        for(Map.Entry<String,String> entry : placeHolders.entrySet()){

            msg = msg.replace("%"+ entry.getKey() + "%",entry.getValue());
        }

        return msg;
    }

    public static EssentialSmp getInstance(){

        return instance;
    }



/*
    public DBManager getDataBase(){
        return dbManager;
    }
    public void createHomesTable() {
        try (Connection connection = dbManager.getConnection();
             Statement st = connection.createStatement()) {

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS homes (
                    uuid VARCHAR(36) NOT NULL,
                    name VARCHAR(64) NOT NULL,
                    world VARCHAR(64) NOT NULL,
                    x DOUBLE NOT NULL,
                    y DOUBLE NOT NULL,
                    z DOUBLE NOT NULL,
                    yaw FLOAT NOT NULL,
                    pitch FLOAT NOT NULL,
                    PRIMARY KEY (uuid, name)
                )
                """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
*/

}
