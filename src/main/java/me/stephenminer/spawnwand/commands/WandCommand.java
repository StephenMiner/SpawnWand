package me.stephenminer.spawnwand.commands;

import me.stephenminer.spawnwand.Spawnwand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Level;

public class WandCommand implements CommandExecutor, TabCompleter {
    private final Spawnwand plugin;

    public WandCommand(Spawnwand plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("wand")){
            int size = args.length;
            if (size >= 1){
                String sub = args[0];
                //setSpawn
                if (sub.equalsIgnoreCase("setSpawn")){
                    if (sender instanceof Player){
                        Player player = (Player) sender;
                        if (!player.hasPermission("spawnwand.commands.wand.setspawn")){
                            player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                            return false;
                        }
                        String locString = plugin.fromLoc(player.getLocation());
                        plugin.settings.getConfig().set("wand.sendTo", locString);
                        plugin.settings.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "Set wand teleport location!");
                        return true;
                    }else sender.sendMessage(ChatColor.RED + "Sorry, but only players can use this command!");
                }

                //give
                if (sub.equalsIgnoreCase("give")){
                    if (sender instanceof Player){
                        Player player = (Player) sender;
                        if (!player.hasPermission("spawnwand.commands.wand.give")){
                            player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                            return false;
                        }
                        int filled = 0;
                        for (ItemStack item : player.getInventory().getContents()){
                            if (item != null && !item.getType().isAir()) filled++;
                        }
                        if (filled < player.getInventory().getSize()){
                            player.getInventory().addItem(plugin.wandItem());
                        }
                    }else sender.sendMessage(ChatColor.RED + "Sorry, but only players can use this command!");
                }
                //reloadConfig
                if (sub.equalsIgnoreCase("reloadConfig")){
                    if (sender instanceof Player){
                        Player player = (Player) sender;
                        if (!player.hasPermission("spawnwand.commands.wand.reloadconfig")){
                            player.sendMessage(ChatColor.RED + "Sorry but you do not have permission to use this command!");
                            return false;
                        }
                    }
                    plugin.settings.reloadConfig();
                    plugin.settings.saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Reloaded config file!");
                    return true;
                }
                //recipe
                if (sub.equalsIgnoreCase("recipe")){
                    if (sender instanceof Player){
                        Player player = (Player) sender;
                        if (!player.hasPermission("spawnwand.commands.wand.recipe")){
                            player.sendMessage(ChatColor.RED + "Sorry but you do not have permission to use this command!");
                            return false;
                        }
                        sendInventory(player);
                        player.sendMessage(ChatColor.GREEN + "Opening recipe editor");
                    }sender.sendMessage(ChatColor.RED + "Sorry, but only players can use this command!");
                }

                if (size >= 2){
                    //wait-time
                    if (sub.equalsIgnoreCase("setWaitTime")){
                        if (sender instanceof Player){
                            Player player = (Player) sender;
                            if (!player.hasPermission("spawnwand.commands.wand.setwaittime")){
                                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                                return false;
                            }
                        }
                        try{
                            int time = Integer.parseInt(args[1]) * 20;
                            plugin.settings.getConfig().set("wand.wait-time", time);
                            plugin.settings.saveConfig();
                            sender.sendMessage(ChatColor.GREEN + "Set wand wait time to " + args[1] + " seconds");
                            return true;
                        }catch (Exception e){
                            plugin.getLogger().log(Level.WARNING, "Attempted to parse integer from " + args[1] + ", but failed");
                        }
                        sender.sendMessage(ChatColor.RED + "You need to input a real number for your second argument!");
                        return false;
                    }

                    //setWaitMessage
                    if (sub.equalsIgnoreCase("setWaitMessage")){
                        if (sender instanceof Player){
                            Player player = (Player) sender;
                            if (!player.hasPermission("spawnwand.commands.wand.setwaitmessage")){
                                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                                return false;
                            }
                        }
                        String removeUnderScore = ChatColor.translateAlternateColorCodes('&', args[1].replace('_', ' '));
                        plugin.settings.getConfig().set("wand.waiting-message", removeUnderScore);
                        plugin.settings.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "Set waiting message to " + removeUnderScore);
                    }

                    //setTeleportMessage
                    if (sub.equalsIgnoreCase("setTeleportMessage")){
                        if (sender instanceof Player){
                            Player player = (Player) sender;
                            if (!player.hasPermission("spawnwand.commands.wand.setteleportmessage")){
                                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                                return false;
                            }
                        }
                        String removeUnderScore = ChatColor.translateAlternateColorCodes('&', args[1].replace('_', ' '));
                        plugin.settings.getConfig().set("wand.teleport-message", removeUnderScore);
                        plugin.settings.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "Set teleport message to " + removeUnderScore);
                    }

                    //setItemName
                    if (sub.equalsIgnoreCase("setItemName")){
                        if (sender instanceof Player){
                            Player player = (Player) sender;
                            if (!player.hasPermission("spawnwand.commands.wand.setitemname")){
                                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                                return false;
                            }
                        }
                        String removeUnderScore = ChatColor.translateAlternateColorCodes('&', args[1].replace('_', ' '));
                        plugin.settings.getConfig().set("wand.item.name", removeUnderScore);
                        plugin.settings.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "Set wand item name " + removeUnderScore);
                    }

                    if (sub.equalsIgnoreCase("setWandMaterial")){
                        if (sender instanceof Player){
                            Player player = (Player) sender;
                            if (!player.hasPermission("spawnwand.commands.wand.setwandmaterial")){
                                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                                return false;
                            }
                        }
                        try{
                            Material mat = Material.matchMaterial(args[1]);
                            plugin.settings.getConfig().set("wand.item.material", mat.name());
                            plugin.settings.saveConfig();
                            sender.sendMessage(ChatColor.GREEN + "Set wand item material " + mat.name());
                            return true;
                        }catch(Exception ignored){

                        }
                        sender.sendMessage(ChatColor.RED + "Inputted material " + args[1] + " isn't a real material!");
                        return false;

                    }

                    //setItemLore
                    if (sub.equalsIgnoreCase("setItemLore")){
                        if (sender instanceof Player){
                            Player player = (Player) sender;
                            if (!player.hasPermission("spawnwand.commands.wand.setitemlore")){
                                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                                return false;
                            }
                        }
                        List<String> lore = new ArrayList<>();
                        for (int i = 1; i < args.length; i++){
                            String entry = ChatColor.translateAlternateColorCodes('&', args[i].replace('_', ' '));
                            lore.add(entry);
                        }
                        plugin.settings.getConfig().set("wand.item.lore", lore);
                        plugin.settings.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "Set item lore");
                    }



                }
            }
        }
        return false;
    }


    private void sendInventory(Player player){
        Inventory inv = Bukkit.createInventory(null, 45, "Wand Recipe");
        player.openInventory(inv);
    }




    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("wand")){
            int size = args.length;
            if (size == 1) return subCommands(args[0]);
            if (size == 2){
                String sub = args[0];
                if (sub.equalsIgnoreCase("setWaitTime")) return integer();
                if (sub.equalsIgnoreCase("setWaitMessage")) return message();
                if (sub.equalsIgnoreCase("setTeleportMessage")) return message();
                if (sub.equalsIgnoreCase("setWandMaterial")) return materials(args[1]);
                if (sub.equalsIgnoreCase("setItemName")) return name();
            }
            if (size >= 2){
                String sub = args[0];
                if (sub.equalsIgnoreCase("setItemLore")) return lore();
            }

        }
        return null;
    }

    private List<String> filter(Collection<String> base, String match){
        match = match.toLowerCase(Locale.ROOT);
        List<String> filtered = new ArrayList<>();
        for (String entry : base){
            String temp = entry.toLowerCase(Locale.ROOT);
            if (temp.contains(match)) filtered.add(entry);
        }
        return filtered;
    }

    private List<String> materials(String match){
        Set<String> matNames = new HashSet<>();
        for (Material mat : Material.values()){
            matNames.add(mat.name());
        }
        return filter(matNames, match);
    }
    private List<String> subCommands(String match){
        List<String> subs = new ArrayList<>();
        subs.add("recipe");
        subs.add("setSpawn");
        subs.add("give");
        subs.add("setWaitTime");
        subs.add("setWaitMessage");
        subs.add("setTeleportMessage");
        subs.add("setWandMaterial");
        subs.add("setItemName");
        subs.add("setItemLore");
        return filter(subs, match);
    }

    private List<String> integer(){
        List<String> ints = new ArrayList<>();
        ints.add("[real number, time unit is seconds]");
        return ints;
    }
    private List<String> name(){
        List<String> name = new ArrayList<>();
        name.add("[name here (underscores are replaced with spaces)]");
        return name;
    }
    private List<String> message(){
        List<String> message = new ArrayList<>();
        message.add("[message here (underscores are replaced with spaces)]");
        return message;
    }
    private List<String> lore(){
        List<String> lore = new ArrayList<>();
        lore.add("[lore here]");
        lore.add("[each new argument is a line of lore]");
        return lore;
    }

}
