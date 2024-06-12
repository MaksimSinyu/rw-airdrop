package org.freeze.loseairdrop.listeners.player;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.freeze.loseairdrop.LoseAirDrop;
import org.freeze.loseairdrop.managers.airdrop.AirDropManager;

public class PlayerEventsListener implements Listener {
    private JavaPlugin plugin;
    private final AirDropManager airDropManager;


    public PlayerEventsListener(JavaPlugin plugin, AirDropManager airDropManager) {
        this.plugin = plugin;
        this.airDropManager = airDropManager;
        Bukkit.getPluginManager().registerEvents(this, LoseAirDrop.getINSTANCE());
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (airDropManager.getBossDisplayManager().getActiveBossBar() != null) {
            airDropManager.getBossDisplayManager().getActiveBossBar().addPlayer(event.getPlayer());
        }
    }

        @EventHandler
        public void onItemPickup(PlayerPickupItemEvent event) {
            ItemStack item = event.getItem().getItemStack();
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(plugin, "unique_id");

                if (container.has(key, PersistentDataType.STRING)) {
                    container.remove(key);
                    item.setItemMeta(meta);
                }
            }
        }
    }