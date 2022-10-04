package me.stephenminer.spawnwand.event;

import com.sun.tools.javac.jvm.Items;
import me.stephenminer.spawnwand.Spawnwand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerEvents implements Listener {
    private final Spawnwand plugin;
    private final HashMap<UUID, Long> cooldowns;

    public PlayerEvents(Spawnwand plugin){
        this.plugin = plugin;
        cooldowns = new HashMap<>();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        ItemStack item = event.getItemInHand();
        if (hasLore(item, "wand")){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if (!event.hasItem()) return;
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (hasLore(item, "wand")){
            if (cooldowns.containsKey(player.getUniqueId())){
                if (cooldowns.get(player.getUniqueId()) > System.currentTimeMillis()){
                    player.sendMessage(ChatColor.RED + "Please wait before using the wand again");
                    return;
                }
            }
            int countTo = 200;
            if (plugin.settings.getConfig().contains("wand.wait-time"))
                countTo = plugin.settings.getConfig().getInt("wand.wait-time");
            final int max = countTo;
            Location loc = null;
            if (plugin.settings.getConfig().contains("wand.sendTo")){
                loc = plugin.fromString(plugin.settings.getConfig().getString("wand.sendTo"));
            }else loc = Bukkit.getWorlds().get(0).getSpawnLocation();
            final Location sendTo = loc;
            new BukkitRunnable(){
                int count = 0;

                @Override
                public void run(){
                    if (!player.isOnline() || player.isDead()){
                        this.cancel();
                        return;
                    }
                    if (count % 20 == 0){
                        if (plugin.settings.getConfig().contains("wand.waiting-message")) {
                            String msg = plugin.settings.getConfig().getString("wand.waiting-message")
                                    .replace("[seconds]", "" + (max-count)/20)
                                    .replace("[world]", sendTo.getWorld().getName());
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                        }
                    }
                    if (count >= max){
                        if (plugin.settings.getConfig().contains("wand.teleport-message")) {
                            String msg = plugin.settings.getConfig().getString("wand.teleport-message")
                                    .replace("[world]", sendTo.getWorld().getName());
                            player.teleport(sendTo);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',msg));
                            player.getInventory().remove(item);
                            this.cancel();
                            return;
                        }
                    }
                    count++;
                }
            }.runTaskTimer(plugin, 1, 1);
            cooldowns.put(player.getUniqueId(), (countTo / 20) * 1000 + System.currentTimeMillis());
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        cooldowns.remove(player.getUniqueId());
    }

    private boolean hasLore(ItemStack item, String target){
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) return false;
        List<String> lore = item.getItemMeta().getLore();
        for (String entry : lore){
            String temp = ChatColor.stripColor(entry);
            if (temp.equalsIgnoreCase(target)) return true;
        }
        return false;
    }
}
