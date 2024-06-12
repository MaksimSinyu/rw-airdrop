package org.freeze.loseairdrop.bosses;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.freeze.loseairdrop.bosses.abilities.Ability;
import org.freeze.loseairdrop.listeners.boss.BossListener;
import org.freeze.loseairdrop.listeners.boss.chunk.BossChunkListener;

import java.util.List;

public abstract class Boss {
    protected JavaPlugin plugin;
    @Getter
    protected LivingEntity entity;
    @Getter
    protected String customName;
    @Getter
    protected String type;

    public Boss(JavaPlugin plugin) {
        this.plugin = plugin;
        new BossListener(plugin ,this);
        new BossChunkListener(plugin, this, 50);
    }

    public abstract void onDeath();

    public abstract void spawn(World world, Location location);
    public abstract List<Ability> getAbilities();

    protected void strikeLightning(Location location) {
        location.getWorld().strikeLightningEffect(location);
    }
}