package org.freeze.loseairdrop.managers.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.freeze.loseairdrop.managers.config.ConfigManager;

import java.util.List;

public class HelpCommandManager {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;

    public HelpCommandManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void sendHelpMessage(Player player) {
        List<String> messages = configManager.getHelpMessages();
        for (String message : messages) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
}
