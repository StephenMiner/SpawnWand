package me.stephenminer.spawnwand;

import me.stephenminer.spawnwand.commands.WandCommand;
import me.stephenminer.spawnwand.event.InventoryEvents;
import me.stephenminer.spawnwand.event.PlayerEvents;
import me.stephenminer.spawnwand.recipe.RecipeBuilder;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class Spawnwand extends JavaPlugin {

    public ConfigFile settings;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.settings = new ConfigFile(this, "settings");
        registerEvents();
        registerCommands();
        loadRecipe();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerEvents(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerEvents(this), this);
        pm.registerEvents(new InventoryEvents(this), this);
    }
    private void registerCommands(){
        WandCommand wandCommand = new WandCommand(this);
        getCommand("wand").setExecutor(wandCommand);
        getCommand("wand").setTabCompleter(wandCommand);
    }

    private void loadRecipe(){
        RecipeBuilder builder = new RecipeBuilder(this, wandItem());
        builder.buildRecipe();
    }


    public Location fromString(String str){
        String[] contents = str.split(",");
        double x = Double.parseDouble(contents[1]);
        double y = Double.parseDouble(contents[2]);
        double z = Double.parseDouble(contents[3]);
        float yaw = Float.parseFloat(contents[4]);
        float pitch = Float.parseFloat(contents[5]);
        World world = null;
        try{
            Bukkit.getWorld(contents[0]);
        }catch (Exception e){
            getLogger().log(Level.WARNING, "World from spawn config " + contents[0] + " doesn't exist or isn't loaded, attempting to load world now!");
        }
        world = new WorldCreator(contents[0]).createWorld();
        return new Location(world, x, y, z, yaw, pitch);
    }

    public String fromLoc(Location loc){
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ()
                + "," + loc.getYaw() + "," + loc.getPitch();
    }

    public ItemStack wandItem(){
        Material mat = Material.BLAZE_ROD;
        if (settings.getConfig().contains("wand.item.material"))
            mat = Material.matchMaterial(settings.getConfig().getString("wand.item.material"));
        String name = ChatColor.GOLD + "Spawn Wand";
        if (settings.getConfig().contains("wand.item.name"))
            name = ChatColor.translateAlternateColorCodes('&', settings.getConfig().getString("wand.item.name"));
        List<String> lore = new ArrayList<>();
        if (settings.getConfig().contains("wand.item.lore")){
            for (String entry : settings.getConfig().getStringList("wand.item.lore")){
                lore.add(ChatColor.translateAlternateColorCodes('&', entry));
            }
        }
        lore.add(ChatColor.BLACK + "wand");
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
