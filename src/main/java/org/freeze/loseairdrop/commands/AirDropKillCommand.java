package org.freeze.loseairdrop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.freeze.loseairdrop.managers.bossbar.BossDisplayManager;

public class AirDropKillCommand implements CommandExecutor {
    private final BossDisplayManager bossDisplayManager;

    public AirDropKillCommand(BossDisplayManager bossDisplayManager) {
        this.bossDisplayManager = bossDisplayManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("kill")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (bossDisplayManager != null) {
                    bossDisplayManager.killCurrentBoss();
                    player.sendMessage("Босс убит и стадия изменена.");
                } else {
                    player.sendMessage("Не удалось найти диспетчера отображения босса.");
                }
            } else {
                sender.sendMessage("Эту команду может выполнить только игрок.");
            }
            return true;
        }
        return false;
    }
}
