package org.freeze.loseairdrop.listeners.region;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.freeze.loseairdrop.LoseAirDrop;
import org.freeze.loseairdrop.flags.CustomFlags;

public class PlayerFlightListener implements Listener {

    private final LoseAirDrop plugin;

    public PlayerFlightListener(LoseAirDrop plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        handleFlight(event.getPlayer(), event.getTo());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        handleFlight(event.getPlayer(), event.getTo());
    }

    private void handleFlight(Player player, Location to) {
        if (to == null) {
            return;
        }

        if (!player.getWorld().equals(to.getWorld())) {
            return;
        }

        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

            RegionQuery query = container.createQuery();

            com.sk89q.worldguard.LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

            ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(to));

            boolean isAirDropRegion = set.testState(localPlayer, CustomFlags.NO_FLY);

            if (!isAirDropRegion) {
                if (player.isFlying() || player.getAllowFlight()) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("An error occurred while handling flight: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
