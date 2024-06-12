package org.freeze.loseairdrop.listeners.boss.transform;

import org.bukkit.Bukkit;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.inventory.ItemStack;
import org.freeze.loseairdrop.bosses.Boss;
import org.freeze.loseairdrop.bosses.types.PoseidonBoss;
import org.freeze.loseairdrop.managers.bossbar.BossDisplayManager;

public class ZombieTransformListener implements Listener {

    @EventHandler
    public void onZombieTransform(EntityTransformEvent event) {
        if (event.getTransformReason() == EntityTransformEvent.TransformReason.DROWNED && event.getEntity() instanceof Zombie) {
            Zombie zombie = (Zombie) event.getEntity();

            if (BossDisplayManager.getInstance().isBoss(zombie)) {
                Boss boss = BossDisplayManager.getInstance().getCurrentBoss();

                if (boss instanceof PoseidonBoss) {
                    PoseidonBoss poseidonBoss = (PoseidonBoss) boss;

                    event.setCancelled(true);

                    Bukkit.getScheduler().runTaskLater(BossDisplayManager.getInstance().getPlugin(), () -> {
                        poseidonBoss.equipNetherArmor();
                        poseidonBoss.equipDiamondSword();
                    }, 1L);
                } else {
                    Bukkit.getLogger().warning("Unexpected boss type: " + boss.getClass().getName());
                }
            }
        }
    }


    private String itemToString(ItemStack item) {
        if (item == null) {
            return "null";
        }
        return item.getType().toString() + "x" + item.getAmount();
    }

}
