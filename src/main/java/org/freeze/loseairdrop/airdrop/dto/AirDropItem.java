package org.freeze.loseairdrop.airdrop.dto;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("AirDropItem")
public class AirDropItem implements ConfigurationSerializable {
    private final String item;
    private final int amount;
    private final String itemName;
    private final Map<String, Object> serializedItem;

    public AirDropItem(String item, int amount, String itemName, Map<String, Object> serializedItem) {
        this.item = item;
        this.amount = amount;
        this.itemName = itemName;
        this.serializedItem = serializedItem;
    }

    public String getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public String getItemName() {
        return itemName;
    }

    public Map<String, Object> getSerializedItem() {
        return serializedItem;
    }



    public static AirDropItem fromItemStack(ItemStack itemStack) {
        Map<String, Object> serialized = itemStack.serialize();
        String itemName = itemStack.getItemMeta() != null ? itemStack.getItemMeta().getDisplayName() : null;
        return new AirDropItem(itemStack.getType().toString(), itemStack.getAmount(), itemName, serialized);
    }

    public ItemStack toItemStack() {
        ItemStack itemStack = ItemStack.deserialize(serializedItem);
        itemStack.setAmount(amount);
        return itemStack;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("item", item);
        map.put("amount", amount);
        map.put("itemName", itemName);
        map.put("serialized_item", serializedItem);
        return map;
    }

    public static AirDropItem deserialize(Map<String, Object> map) {
        String item = (String) map.get("item");
        int amount = (int) map.get("amount");
        String itemName = (String) map.get("itemName");
        Map<String, Object> serializedItem = (Map<String, Object>) map.get("serialized_item");
        return new AirDropItem(item, amount, itemName, serializedItem);
    }
}
