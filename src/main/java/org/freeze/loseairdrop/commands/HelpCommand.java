package org.freeze.loseairdrop.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.freeze.loseairdrop.managers.config.ConfigManager;

import java.util.List;

public class HelpCommand implements CommandExecutor {
    private final ConfigManager configManager;

    public HelpCommand(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            List<String> helpMessages = configManager.getHelpMessage();
            for (String message : helpMessages) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        } else {
            sender.sendMessage("This command can only be used by players.");
        }
        return true;
    }
}
