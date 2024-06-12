package org.freeze.loseairdrop.bosses.abilities.poseidon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.freeze.loseairdrop.bosses.abilities.Ability;

import java.util.ArrayList;
import java.util.List;

public class SpawnZombiesAbility implements Ability {
    private World world;
    private JavaPlugin plugin;
    private List<BukkitTask> tasks = new ArrayList<>();

    public SpawnZombiesAbility(World world, JavaPlugin plugin) {
        this.world = world;
        this.plugin = plugin;
    }

    @Override
    public void apply(LivingEntity boss) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                Location spawnLocation = getRandomLocationAroundBoss(boss.getLocation(), 8);
                Zombie zombie = (Zombie) world.spawnEntity(spawnLocation, EntityType.ZOMBIE);
            }
        }.runTaskTimer(plugin, 0L, 200L);;

        tasks.add(task);
    }

    @Override
    public void cancelTasks() {
        for (BukkitTask task : tasks) {
            task.cancel();
        }
    }

    private Location getRandomLocationAroundBoss(Location bossLocation, double radius) {
        double x = bossLocation.getX() + (Math.random() * radius * 2 - radius);
        double z = bossLocation.getZ() + (Math.random() * radius * 2 - radius);
        Location spawnLocation = new Location(bossLocation.getWorld(), x, bossLocation.getY(), z);
        return spawnLocation;
    }
}
