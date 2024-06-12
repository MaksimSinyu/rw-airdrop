package org.freeze.loseairdrop.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.freeze.loseairdrop.managers.config.ConfigManager;

import java.util.List;

public class AirDropReloadCommand implements CommandExecutor {
    private final ConfigManager configManager;

    public AirDropReloadCommand(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        configManager.reloadConfig();

        List<String> messages = configManager.getReloadMessage();
        for (String message : messages) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        return true;
    }
}
