package me.yura.adminpass;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public final class AdminPass extends JavaPlugin implements CommandExecutor {

    public static AdminPass instance;

    public static HashMap<String, String> groupsPasswords = new HashMap<>();
    public static boolean enableAdminPass;

    private static Permission perms;

    public static final ArrayList<String> inQueue = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic

        //Initialize instance
        instance = this;

        //Register events listeners
        Bukkit.getPluginManager().registerEvents(new ListenerClass(), this);

        //Register command executor
        getCommand("apass").setExecutor(this);

        //Load configuration
        File file = new File(getDataFolder() + "/config.yml");
        if(!file.exists()) saveDefaultConfig();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        //Load passwords
        for(String key : config.getConfigurationSection("groups").getKeys(false)){
            groupsPasswords.put(key, config.getString("groups." + key));
        }

        enableAdminPass = config.getBoolean("enableAdminPass", true);

        //Try to set up permissions (Vault required)
        if(setupPermissions()) getLogger().info("Vault hooked!");

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            if(args.length == 1){

                if(!inQueue.contains((sender).getName())){
                    return true;
                }

                String pass = args[0];

                //Get group password
                String groupPass = groupsPasswords.get(getPermissions().getPlayerGroups(((Player) sender))[0]);

                if(groupPass.equals(pass)){
                    //Remove from list
                    inQueue.remove(sender.getName());

                    //Allow pickup items
                    ((Player) sender).setCanPickupItems(true);

                    sender.sendMessage("§aУспешная авторизация!");
                }else{
                    //Kick player
                    ((Player) sender).kickPlayer("§cНеверный пароль!");

                    //Remove from list
                    inQueue.remove(sender.getName());
                }

            }else{
                sender.sendMessage("§cНеверное использование!");
                return true;
            }
        }
        return true;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static AdminPass getInstance() {
        return instance;
    }

    public static Permission getPermissions() {
        return perms;
    }
}
