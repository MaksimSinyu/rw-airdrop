package org.freeze.loseairdrop.listeners.airdropInteract;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.freeze.loseairdrop.LoseAirDrop;
import org.freeze.loseairdrop.airdrop.states.AirDropState;
import org.freeze.loseairdrop.managers.airdrop.AirDropManager;
import org.freeze.loseairdrop.managers.bossbar.BossDisplayManager;

public class AirDropClickListener implements Listener {

    private final BossDisplayManager bossDisplayManager;
    private final LoseAirDrop plugin;

    public AirDropClickListener(BossDisplayManager bossDisplayManager, LoseAirDrop plugin) {
        this.bossDisplayManager = bossDisplayManager;
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.RESPAWN_ANCHOR) {
            AirDropState currentState = bossDisplayManager.getCurrentState();
            if (currentState == AirDropState.BOSS_FIGHT || currentState == AirDropState.WAITING_TIME) {
                Player player = event.getPlayer();
                String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("airdrop.touch"));
                player.sendMessage(message);
                event.setCancelled(true);
            }
        }
    }
}
