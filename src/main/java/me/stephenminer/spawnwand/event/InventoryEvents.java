package me.stephenminer.spawnwand.event;

import me.stephenminer.spawnwand.Spawnwand;
import me.stephenminer.spawnwand.recipe.RecipeBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Level;

public class InventoryEvents implements Listener {
    private final Spawnwand plugin;
    public InventoryEvents(Spawnwand plugin){
        this.plugin = plugin;
    }


    @EventHandler
    public void onOpen(InventoryOpenEvent event){
        String title = event.getView().getTitle();
        Inventory inv = event.getInventory();
        if (title.equalsIgnoreCase("Wand Recipe")) {
            for (int i = 0; i < inv.getSize(); i++){
                inv.setItem(i, filler());
            }
            ItemStack[] row1 = recipeRow(1);
            ItemStack[] row2 = recipeRow(2);
            ItemStack[] row3 = recipeRow(3);
            for (int i = 12; i < 15; i++){
                inv.setItem(i, row1[i-12]);
            }
            for (int i = 21; i < 24; i++){
                inv.setItem(i, row2[i-21]);
            }
            for (int i = 30; i < 33; i++){
                inv.setItem(i, row3[i-30]);
            }
            inv.setItem(40, exit());
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        int slot = event.getSlot();
        if (event.getClickedInventory() == null) return;
        String title = event.getView().getTitle();
        if (!title.equalsIgnoreCase("Wand Recipe")) return;
        if (!((slot >= 12 && slot < 15) || (slot >= 21 && slot < 24) || (slot >= 30 && slot < 33))){
            event.setCancelled(true);
            if (slot == 40){
                Player player = (Player) event.getWhoClicked();
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        String title = event.getView().getTitle();
        if (title.equalsIgnoreCase("Wand Recipe")){
            RecipeBuilder builder = new RecipeBuilder(plugin, event.getInventory(), plugin.wandItem());
            builder.buildRecipe();
            builder.save();
            event.getPlayer().sendMessage(ChatColor.GREEN + "Updated wand recipe!");
        }
    }

    private ItemStack filler(){
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack exit(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Exit");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack[] recipeRow(int row){
        ItemStack[] items = new ItemStack[3];
        String[] asStrings = plugin.settings.getConfig().getStringList("wand.recipe").get(row-1).split(",");
        for (int i = 0; i < asStrings.length; i++){
            String entry = asStrings[i];
            if (entry.equals(" ")){
                items[i] = new ItemStack(Material.AIR);
                continue;
            }
            try{
                items[i] = new ItemStack(Material.matchMaterial(entry));
            }catch (Exception ignored){
                plugin.getLogger().log(Level.WARNING, "Attempted to get material " + entry + ", but material doesn't exist!");
            }
        }
        return items;
    }
}
