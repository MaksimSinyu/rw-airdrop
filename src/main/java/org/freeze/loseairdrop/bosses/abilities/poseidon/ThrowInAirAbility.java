package org.freeze.loseairdrop.bosses.abilities.poseidon;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import java.util.Random;
import org.freeze.loseairdrop.bosses.abilities.Ability;

public class ThrowInAirAbility implements Ability {
    private final double power;
    private final double chance;
    public ThrowInAirAbility(double power, double chance) {
        this.power = power;
        this.chance = chance;
    }

    @Override
    public void apply(LivingEntity target) {
        Bukkit.getLogger().info("THROWING!");
        if (new Random().nextDouble() < chance) {
            Vector vector = target.getVelocity();
            vector.setY(power);
            target.setVelocity(vector);
        }
    }

    @Override
    public void cancelTasks() {
        return;
    }
}
