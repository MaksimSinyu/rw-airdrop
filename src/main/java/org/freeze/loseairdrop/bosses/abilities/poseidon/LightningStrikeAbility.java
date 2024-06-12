package org.freeze.loseairdrop.bosses.abilities.poseidon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.freeze.loseairdrop.bosses.abilities.Ability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LightningStrikeAbility implements Ability {
    private final JavaPlugin plugin;
    private final double radius;
    private final double damage;
    private final double verticalVelocity;
    private List<BukkitTask> tasks = new ArrayList<>();

    public LightningStrikeAbility(JavaPlugin plugin, double radius, double damage, double verticalVelocity) {
        this.plugin = plugin;
        this.radius = radius;
        this.damage = damage;
        this.verticalVelocity = verticalVelocity;
    }

    @Override
    public void apply(LivingEntity boss) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                Location bossLocation = boss.getLocation();
                Collection<Entity> nearbyEntities = bossLocation.getWorld().getNearbyEntities(bossLocation, radius, radius, radius);

                for (Entity entity : nearbyEntities) {
                    if (entity instanceof LivingEntity && entity != boss) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        livingEntity.damage(damage);
                        livingEntity.setVelocity(new Vector(0, verticalVelocity, 0));
                    }
                }

                bossLocation.getWorld().strikeLightningEffect(bossLocation);
            }
        }.runTaskTimer(plugin, 0L, 80L);

        tasks.add(task);
    }

    @Override
    public void cancelTasks() {
        for (BukkitTask task : tasks) {
            task.cancel();
        }
    }
}