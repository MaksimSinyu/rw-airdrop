package org.freeze.loseairdrop.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.freeze.loseairdrop.managers.airdrop.AirDropManager;

import java.util.Random;

public class AirDropTask implements Runnable {

    private final JavaPlugin plugin;
    private final AirDropManager airDropManager;
    private int taskId = -1;

    public AirDropTask(JavaPlugin plugin, AirDropManager airDropManager) {
        this.plugin = plugin;
        this.airDropManager = airDropManager;
    }

    @Override
    public void run() {
        if (airDropManager.hasActiveAirDrop()) {
            scheduleTask(216000L);
        } else {
            int onlinePlayers = Bukkit.getOnlinePlayers().size();
            if (onlinePlayers < 30) {
                scheduleTask(216000L);
            } else {
                spawnRandomAirDrop();
            }
        }
    }

    public void scheduleTask(long delayTicks) {
        if (taskId != -1) {
            cancelTask();
        }
        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this, delayTicks);
    }

    public void cancelTask() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    private void spawnRandomAirDrop() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            World world = Bukkit.getWorld("world");
            if (world == null) {
                plugin.getLogger().warning("Мир не найден!");
                return;
            }

            Random random = new Random();
            int x = random.nextInt(10000) - 5000;
            int z = random.nextInt(10000) - 5000;

            Chunk chunk = world.getChunkAt(x >> 4, z >> 4);
            if (!chunk.isLoaded()) {
                chunk.load(true);
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                int y = world.getHighestBlockYAt(x, z);
                Location location = new Location(world, x, y, z);
                airDropManager.createRandomAirDrop(world, x, y, z);
                scheduleTask(216000L);
            });
        });
    }
}
