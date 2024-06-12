package org.freeze.loseairdrop.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.freeze.loseairdrop.managers.airdrop.AirDropManager;
import org.freeze.loseairdrop.managers.bossbar.BossDisplayManager;
import org.freeze.loseairdrop.managers.config.ConfigManager;
import org.freeze.loseairdrop.managers.database.DatabaseManager;

public class LoseAirCommand implements CommandExecutor {
    private final AirDropManager airDropManager;
    private final DatabaseManager databaseManager;
    private final ConfigManager configManager;
    private final BossDisplayManager bossDisplayManager;
    private final JavaPlugin plugin;

    public LoseAirCommand(AirDropManager airDropManager, DatabaseManager databaseManager, ConfigManager configManager, BossDisplayManager bossDisplayManager, JavaPlugin plugin) {
        this.airDropManager = airDropManager;
        this.databaseManager = databaseManager;
        this.configManager = configManager;
        this.bossDisplayManager = bossDisplayManager;
        this.plugin = plugin;
    }

    private void sendNoPermissionMessage(CommandSender sender) {
        String noPermissionMessage = configManager.getNoPermissionMessage();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermissionMessage));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Use /loseair <help|spawn|open|reload|remove>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "help":
                if (sender.hasPermission("loseair.help")) {
                    return new HelpCommand(configManager).onCommand(sender, command, label, args);
                } else {
                    sendNoPermissionMessage(sender);
                    return true;
                }
            case "spawn":
                if (sender.hasPermission("loseair.spawn")) {
                    return new AirDropSpawnCommand(airDropManager, configManager).onCommand(sender, command, label, args);
                } else {
                    sendNoPermissionMessage(sender);
                    return true;
                }
            case "open":
                if (sender.hasPermission("loseair.open")) {
                    return new AirDropOpenCommand(databaseManager, configManager, plugin).onCommand(sender, command, label, args);
                } else {
                    sendNoPermissionMessage(sender);
                    return true;
                }
            case "reload":
                if (sender.hasPermission("loseair.reload")) {
                    return new AirDropReloadCommand(configManager).onCommand(sender, command, label, args);
                } else {
                    sendNoPermissionMessage(sender);
                    return true;
                }
            case "remove":
                if (sender.hasPermission("loseair.remove")) {
                    return new AirDropRemoveCommand(bossDisplayManager, configManager).onCommand(sender, command, label, args);
                } else {
                    sendNoPermissionMessage(sender);
                    return true;
                }
            case "kill":
                if (sender.hasPermission("loseair.kill")) {
                    return new AirDropKillCommand(bossDisplayManager).onCommand(sender, command, label, args);
                } else {
                    sendNoPermissionMessage(sender);
                    return true;
                }
            default:
                sender.sendMessage("Unknown subcommand. Use /loseair <help|spawn|open|reload|remove>");
                return true;
        }
    }
}
