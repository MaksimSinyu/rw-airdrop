package org.freeze.loseairdrop.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.ChatColor;
import org.freeze.loseairdrop.managers.airdrop.AirDropManager;
import org.freeze.loseairdrop.managers.bossbar.BossDisplayManager;
import org.freeze.loseairdrop.managers.config.ConfigManager;

public class AirDropRemoveCommand implements CommandExecutor {
    private final BossDisplayManager bossDisplayManager;
    private final ConfigManager configManager;

    public AirDropRemoveCommand(BossDisplayManager bossDisplayManager, ConfigManager configManager) {
        this.bossDisplayManager = bossDisplayManager;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Bukkit.getLogger().info("Executing AirDropRemoveCommand by: " + sender.getName());
        if (!bossDisplayManager.removeAirDrop()) {
            configManager.getRemoveSuccessMessage(false);
            return true;
        }

        configManager.getRemoveSuccessMessage(true);
        return true;
    }
}
