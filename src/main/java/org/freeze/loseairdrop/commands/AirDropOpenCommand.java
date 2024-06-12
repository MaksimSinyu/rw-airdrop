package org.freeze.loseairdrop.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.freeze.loseairdrop.airdrop.dto.AirDropItem;
import org.freeze.loseairdrop.managers.config.ConfigManager;
import org.freeze.loseairdrop.managers.database.DatabaseManager;

import java.util.List;

public class AirDropOpenCommand implements CommandExecutor {
    private final DatabaseManager databaseManager;
    private final ConfigManager configManager;
    private final JavaPlugin plugin;

    public AirDropOpenCommand(DatabaseManager databaseManager, ConfigManager configManager, JavaPlugin plugin) {
        this.databaseManager = databaseManager;
        this.configManager = configManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 2) {
            String type = args[1];
            if (!configManager.isValidAirDropType(type)) {
                player.sendMessage("Неверный тип аирдропа. Пожалуйста, выберите допустимый тип.");
                return true;
            }
            List<AirDropItem> items = databaseManager.getItemsForType(type);
            Inventory inventory = Bukkit.createInventory(null, 54, "AirDrop: " + type);
            for (AirDropItem item : items) {
                inventory.addItem(item.toItemStack());
            }

            player.openInventory(inventory);

            List<String> messages = configManager.getOpenMessage();
            for (String message : messages) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{airdropType}", type)));
            }

            return true;
        }

        return false;
    }
}
