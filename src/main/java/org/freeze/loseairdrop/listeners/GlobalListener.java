package org.freeze.loseairdrop.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.freeze.loseairdrop.airdrop.AirDrop;
import org.freeze.loseairdrop.airdrop.states.AirDropState;
import org.freeze.loseairdrop.bosses.Boss;
import org.freeze.loseairdrop.bosses.abilities.Ability;
import org.freeze.loseairdrop.managers.airdrop.AirDropManager;

import java.util.List;

public class GlobalListener implements Listener {
    private AirDropManager airDropManager;

    public GlobalListener(AirDropManager airDropManager) {
        this.airDropManager = airDropManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.RESPAWN_ANCHOR) {
                event.setCancelled(true);
                AirDrop airDrop = airDropManager.getActiveAirDrop();
                if (airDrop != null && airDropManager.getAirDropState() == AirDropState.WAITING_ACTIVATION) {
                    airDropManager.activateAirDrop(event.getPlayer().getLocation(), event.getPlayer());
                }
            }
        }
    }


}