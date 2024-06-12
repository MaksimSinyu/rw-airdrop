package org.freeze.loseairdrop.managers.database;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.freeze.loseairdrop.airdrop.dto.AirDropItem;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DatabaseManager {
    private JavaPlugin plugin;
    private File dataFile;
    private FileConfiguration dataConfig;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setupYml();
    }

    private void setupYml() {
        dataFile = new File(plugin.getDataFolder(), "airdrop_items.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    private void reloadYml() {
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public List<AirDropItem> getItemsForType(String type) {
        reloadYml();
        List<AirDropItem> items = new ArrayList<>();
        if (dataConfig.contains(type)) {
            List<Map<?, ?>> itemList = dataConfig.getMapList(type);
            for (Map<?, ?> itemMap : itemList) {
                String item = (String) itemMap.get("item");
                int amount = (int) itemMap.get("amount");
                String itemName = (String) itemMap.get("itemName");
                Map<String, Object> serializedItem = (Map<String, Object>) itemMap.get("serialized_item");
                items.add(new AirDropItem(item, amount, itemName, serializedItem));
            }
        }
        return items;
    }


    public void saveItems(String type, ItemStack[] items) {
        List<Map<String, Object>> itemList = new ArrayList<>();
        Arrays.stream(items).filter(item -> item != null && item.getAmount() > 0).forEach(item -> {
            AirDropItem adItem = AirDropItem.fromItemStack(item);
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("item", item.getType().toString());
            itemMap.put("amount", item.getAmount());
            itemMap.put("itemName", item.getItemMeta() != null ? item.getItemMeta().getDisplayName() : null);
            itemMap.put("serialized_item", adItem.getSerializedItem());
            itemList.add(itemMap);
        });
        dataConfig.set(type, itemList);
        saveDataConfig();
    }

    public List<AirDropItem> getAirDropItems(String airdropType) {
        DatabaseManager dbManager = new DatabaseManager(plugin);
        return dbManager.getItemsForType(airdropType);
    }

    public Set<String> getAllAirDropTypes() {
        reloadYml();
        return dataConfig.getKeys(false);
    }


    private void saveDataConfig() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
