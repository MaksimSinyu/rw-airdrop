package org.freeze.loseairdrop.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.freeze.loseairdrop.LoseAirDrop;
import org.freeze.loseairdrop.managers.airdrop.AirDropManager;
import org.freeze.loseairdrop.managers.config.ConfigManager;

import java.util.List;
import java.util.Random;

public class AirDropSpawnCommand implements CommandExecutor {
    private final AirDropManager airDropManager;
    private final ConfigManager configManager;
    private final Random random;

    public AirDropSpawnCommand(AirDropManager airDropManager, ConfigManager configManager) {
        this.airDropManager = airDropManager;
        this.configManager = configManager;
        this.random = new Random();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("loseair.spawn")) {
            String noPermissionMessage = configManager.getNoPermissionMessage();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermissionMessage));
            return true;
        }

        World world = Bukkit.getWorlds().get(0);

        int x, y, z;
        if (args.length == 4) {
            try {
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
                z = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid coordinates. Please provide valid integers.");
                return true;
            }
        } else {
            x = random.nextInt(world.getMaxHeight() - 1);
            z = random.nextInt(world.getMaxHeight() - 1);
            y = world.getHighestBlockYAt(x, z);
        }

        airDropManager.createRandomAirDrop(world, x, y, z);

        List<String> messages = configManager.getSpawnMessage();
        for (String message : messages) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        LoseAirDrop.resetAirDropTask();

        return true;
    }
}
