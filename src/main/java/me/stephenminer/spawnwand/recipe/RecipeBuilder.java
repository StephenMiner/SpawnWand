package me.stephenminer.spawnwand.recipe;

import com.sun.tools.javac.jvm.Items;
import me.stephenminer.spawnwand.Spawnwand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class RecipeBuilder {
    private final Spawnwand plugin;
    private final ItemStack output;

    private ItemStack[] row1;
    private ItemStack[] row2;
    private ItemStack[] row3;

    public RecipeBuilder(Spawnwand plugin, Inventory inventory, ItemStack output){
        this.plugin = plugin;
        this.output = output;
        initArrays();
        if (inventory.getSize() < 45) return;
        fromInventory(inventory);
    }

    public RecipeBuilder(Spawnwand plugin, ItemStack output){
        this.plugin = plugin;
        this.output = output;
        initArrays();
        fromFile();
    }
    private void initArrays(){
        row1 = new ItemStack[3];
        row2 = new ItemStack[3];
        row3 = new ItemStack[3];
    }

    public void fromInventory(Inventory inv){
        for (int i = 12; i < 15; i++){
            row1[i-12] = inv.getItem(i);
        }
        for (int i = 21; i < 24; i++){
            row2[i-21] = inv.getItem(i);
        }
        for (int i = 30; i < 33; i++){
            row3[i-30] = inv.getItem(i);
        }
    }
    public void fromFile(){
        List<String> rows = plugin.settings.getConfig().getStringList("wand.recipe");
        String[] r1 = rows.get(0).split(",");
        row1 = setRows(rows, 1);
        row2 = setRows(rows, 2);
        row3 = setRows(rows, 3);
    }

    private ItemStack[] setRows(List<String> rows, int row){
        ItemStack[] items = new ItemStack[3];
        String[] r1 = rows.get(row-1).split(",");
        for (int i = 0; i < r1.length; i++){
            if (r1[i].equals(" ")) {
                items[i] = new ItemStack(Material.AIR);
                continue;
            }
            try{
                Material mat = Material.matchMaterial(r1[i]);
                items[i] = new ItemStack(mat);
            }catch (Exception e){
                plugin.getLogger().log(Level.WARNING, "Attempted to get material " + r1[i] + ", but material doesn't exist!");
            }
        }
        return items;
    }
    public void buildRecipe(){
        NamespacedKey key = new NamespacedKey(plugin, "spawn_wand");
        ShapedRecipe recipe = new ShapedRecipe(key, output);
        recipe.shape("ABC","DEF","GHI");
        if (row1[0] != null)
            recipe.setIngredient('A', row1[0].getType());
        if (row1[1] != null)
            recipe.setIngredient('B', row1[1].getType());
        if (row1[2] != null)
            recipe.setIngredient('C', row1[2].getType());
        if (row2[0] != null)
            recipe.setIngredient('D', row2[0].getType());
        if (row2[1] != null)
            recipe.setIngredient('E', row2[1].getType());
        if (row2[2] != null)
            recipe.setIngredient('F', row2[2].getType());
        if (row3[0] != null)
            recipe.setIngredient('G', row3[0].getType());
        if (row3[1] != null)
            recipe.setIngredient('H', row3[1].getType());
        if (row3[2] != null)
            recipe.setIngredient('I', row3[2].getType());
        if (Bukkit.getRecipe(key) == null){
            Bukkit.addRecipe(recipe);
        }else{
            Bukkit.removeRecipe(key);
            Bukkit.addRecipe(recipe);
        }
    }

    public String getAsString(int row){
        ItemStack[] stacks = row == 1 ? row1 : row == 2 ? row2 : row3;
        String rowString;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < stacks.length; i++){
            ItemStack item = stacks[i];
            String matName = item != null ? item.getType().name() : " ";
            if (i == stacks.length-1){
                builder.append(matName);
            }else builder.append(matName).append(",");
        }
        rowString = new String(builder);
        return rowString;
    }
    public void save(){
        List<String> rows = new ArrayList<>();
        for (int i = 1; i <= 3; i++){
            rows.add(getAsString(i));
        }
        plugin.settings.getConfig().set("wand.recipe", rows);
        plugin.settings.saveConfig();
    }


}
