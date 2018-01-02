package io.github.scarger.referme;

import io.github.scarger.referme.commands.SubCommand;
import io.github.scarger.referme.interaction.ClickHandler;
import io.github.scarger.referme.storage.ConfigurationStorage;
import io.github.scarger.referme.storage.JsonStorage;
import io.github.scarger.referme.storage.Storage;
import io.github.scarger.referme.management.PermissionsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Synch on 2017-10-14.
 */
public class ReferME {

    //global instance var
    private static ReferME instance;

    //other vars..
    private List<SubCommand> commands;
    private List<ClickHandler> clickHandlers;
    private JsonStorage.Wrapper jsonStorage;
    private JsonStorage.Wrapper jsonConfiguration;
    private Storage storage;
    private ConfigurationStorage configurationStorage;

    //vault/permissions
    private boolean hasVault;
    private PermissionsManager permissionsManager;

    private ReferME(){
        initStorage();
        //collections..
        clickHandlers = new ArrayList<>();
        commands = new ArrayList<>();
        //hook
        hasVault = setupPermissions();
        if(hasVault){
            Bukkit.getLogger().info(configurationStorage.getPrefix()+ChatColor.GREEN +
                    "Successfully hooked into vault for permissions!");
        }
        else{
            Bukkit.getLogger().info(configurationStorage.getPrefix()+ChatColor.RED +
                    "Failed to locate vault. Some permission functionality may fail");
        }
    }

    private boolean setupPermissions() {
        permissionsManager = new PermissionsManager("net.milkbowl.vault.permission.Permission");
        return permissionsManager.getRaw() != null;
    }

    public static ReferME get(){
        if(instance == null){
            instance = new ReferME();
        }
        return instance;
    }

    public void initStorage(){
        File folder = Loader.getPlugin(Loader.class).getDataFolder();
        if(!folder.exists()){
            folder.mkdirs();
        }
        //more init...
        jsonStorage = new JsonStorage.Wrapper("data.json",Storage.class);
        jsonConfiguration = new JsonStorage.Wrapper("config.json",ConfigurationStorage.class);
        //deserialize info...
        storage = (Storage) jsonStorage.getStorageSection();
        configurationStorage = (ConfigurationStorage) jsonConfiguration.getStorageSection();
        //throw updated version on disk (in case of one)
        jsonConfiguration.getStorage().write(configurationStorage);
    }

    public JsonStorage getJsonStorage() {
        return jsonStorage.getStorage();
    }

    public Storage getStorage(){
        return storage;
    }

    public ConfigurationStorage getConfig(){
        return configurationStorage;
    }

    public List<SubCommand> getCommands(){
        return commands;
    }

    public List<ClickHandler> getClickHandlers(){
        return clickHandlers;
    }

    public boolean hasVault(){
        return hasVault;
    }

    public PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }
}
