package org.freeze.loseairdrop.managers.message;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.freeze.loseairdrop.airdrop.AirDrop;
import org.freeze.loseairdrop.airdrop.dto.AirDropItem;
import org.freeze.loseairdrop.hooks.PlaceholderAPIHook;
import org.freeze.loseairdrop.managers.config.ConfigManager;
import org.freeze.loseairdrop.managers.database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MessageManager {

    private final ConfigManager configManager;
    private final PlaceholderAPIHook placeholderAPIHook;
    private final DatabaseManager databaseManager;
    @Getter
    private List<AirDropItem> selectedItems;

    public MessageManager(ConfigManager configManager, DatabaseManager databaseManager) {
        this.configManager = configManager;
        this.placeholderAPIHook = new PlaceholderAPIHook();
        this.databaseManager = databaseManager;
    }

    public void broadcastAirDropSpawnMessage(AirDrop airDrop) {
        List<String> messages = new ArrayList<>(configManager.getAirDropSpawnMessages());
        String displayName = airDrop.getType();
        String type = configManager.getAirDropTypeByDisplayName(displayName);
        List<AirDropItem> items = databaseManager.getAirDropItems(type);
        selectedItems = getRandomItems(items);
        List<String> rewardMessages = formatAirDropRewardsMessage(selectedItems);

        messages.addAll(2, rewardMessages);
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (String message : messages) {
                String replacedMessage = message.replace("%name%", airDrop.getType());
                replacedMessage = placeholderAPIHook.setPlaceholders(player, replacedMessage);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', replacedMessage));
            }
        }
    }

    public List<AirDropItem> getRandomItems(List<AirDropItem> items) {
        List<AirDropItem> selectedItems = new ArrayList<>();
        Random random = new Random();

        for (AirDropItem item : items) {
            if (random.nextDouble() <= 0.7) {
                int randomAmount = 1 + random.nextInt(item.getAmount());
                selectedItems.add(new AirDropItem(item.getItem(), randomAmount, item.getItemName(), item.getSerializedItem()));
            }
        }
        return selectedItems;
    }

    private List<String> formatAirDropRewardsMessage(List<AirDropItem> items) {
        List<AirDropItem> playerHeads = new ArrayList<>();
        List<AirDropItem> otherItems = new ArrayList<>();

        for (AirDropItem item : items) {
            if (Objects.equals(item.getItem(), "PLAYER_HEAD")) {
                playerHeads.add(item);
            } else {
                otherItems.add(item);
            }
        }

        List<AirDropItem> sortedItems = new ArrayList<>();
        sortedItems.addAll(playerHeads);
        sortedItems.addAll(otherItems);

        List<String> messages = new ArrayList<>();
        int itemCount = sortedItems.size();

        for (int i = 0; i < Math.min(3, itemCount); i++) {
            AirDropItem item = sortedItems.get(i);
            String itemName = item.getItemName() != null && !item.getItemName().isEmpty() ? item.getItemName() : item.getItem();
            String formattedItem = String.format("&6Аирдроп &7» %s &7x&6%d", itemName, item.getAmount());
            messages.add(ChatColor.translateAlternateColorCodes('&', formattedItem));
        }

        if (itemCount > 3) {
            String remainingItemsMessage = String.format("&6Аирдроп &7» &fИ еще &6%d &fнаград!", itemCount - 3);
            messages.add(ChatColor.translateAlternateColorCodes('&', remainingItemsMessage));
        }

        return messages;
    }
}
