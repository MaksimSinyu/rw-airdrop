package org.freeze.loseairdrop.listeners.boss;

import org.bukkit.Bukkit;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.freeze.loseairdrop.LoseAirDrop;
import org.freeze.loseairdrop.bosses.Boss;
import org.freeze.loseairdrop.bosses.abilities.Ability;
import org.freeze.loseairdrop.bosses.abilities.poseidon.LightningStrikeAbility;
import org.freeze.loseairdrop.bosses.abilities.poseidon.SpawnZombiesAbility;
import org.freeze.loseairdrop.managers.airdrop.AirDropManager;
import org.freeze.loseairdrop.managers.bossbar.BossDisplayManager;
import org.freeze.loseairdrop.managers.config.ConfigManager;
import org.freeze.loseairdrop.airdrop.states.AirDropState;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.List;

public class BossListener implements Listener {
    protected JavaPlugin plugin;
    private final AirDropManager airDropManager;
    private final ConfigManager configManager;
    private final Boss boss;
    private BossDisplayManager bossDisplayManager;

    public BossListener(JavaPlugin plugin, Boss boss) {
        this.plugin = plugin;
        this.boss = boss;
        this.airDropManager = LoseAirDrop.getINSTANCE().getAirDropManager();
        this.configManager = LoseAirDrop.getConfigManager();
        this.bossDisplayManager = LoseAirDrop.getINSTANCE().getBossDisplayManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager().equals(boss.getEntity())) {
            if (event.getEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) event.getEntity();
                applyBossAbilities(boss, target);
            }
        }
    }

    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        if (event.getEntity().equals(boss.getEntity())) {
            event.getDrops().clear();
            event.setDroppedExp(0);

            airDropManager.setAirDropState(AirDropState.WAITING_TIME);
            bossDisplayManager.updateHologramAndBossBar(boss, airDropManager.getAirDropState());
            boss.onDeath();
        }
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent event) {
        Entity mount = event.getMount();
        Entity entity = event.getEntity();
        if (entity.equals(boss.getEntity()) && mount instanceof Boat) {
            event.setCancelled(true);
        }
    }


    private void applyBossAbilities(Boss boss, LivingEntity target) {
        List<Ability> abilities = boss.getAbilities();
        Bukkit.getLogger().info("ABILITIES IS " + abilities);
        if (abilities != null) {
            for (Ability ability : abilities) {
                Bukkit.getLogger().info("ABILITY IS " + ability.toString());
                if(ability instanceof LightningStrikeAbility || ability instanceof SpawnZombiesAbility) {
                    continue;
                }
                ability.apply(target);
            }
        }
    }
}
