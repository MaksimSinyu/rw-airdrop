package org.freeze.loseairdrop.bosses.abilities;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

public interface Ability {
    void apply(LivingEntity target);
    void cancelTasks();
}
