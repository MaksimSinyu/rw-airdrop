package org.freeze.loseairdrop.bosses.factory;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.freeze.loseairdrop.bosses.Boss;
import org.freeze.loseairdrop.bosses.types.PoseidonBoss;
import org.freeze.loseairdrop.managers.config.ConfigManager;

public class BossFactory {
    protected JavaPlugin plugin;

    public BossFactory(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static Boss createBoss(JavaPlugin plugin, String type, World world, Location location, ConfigManager config) {
        switch (type.toLowerCase()) {
            case "poseidon":
                PoseidonBoss boss = new PoseidonBoss(plugin, world ,type);
                boss.setCustomName(config.getBossName(type.toLowerCase()));
                boss.setHealth(config.getBossHealth(type));
                return boss;
            default:
                return null;
        }
    }

}
