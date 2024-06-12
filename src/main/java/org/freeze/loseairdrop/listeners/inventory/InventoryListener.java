package org.freeze.loseairdrop.listeners.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.freeze.loseairdrop.managers.database.DatabaseManager;

public class InventoryListener implements Listener {
    private DatabaseManager databaseManager;

    public InventoryListener(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().startsWith("AirDrop: ")) {
            Inventory inventory = event.getInventory();
            String type = event.getView().getTitle().substring(9);
            ItemStack[] contents = inventory.getContents();
            databaseManager.saveItems(type, contents);
        }
    }
}
