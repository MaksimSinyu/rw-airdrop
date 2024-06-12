package org.freeze.loseairdrop.listeners.boss.chunk;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.freeze.loseairdrop.bosses.Boss;
import org.freeze.loseairdrop.bosses.chunk.BossChunkLoader;

public class BossChunkListener implements Listener {
    private final Boss boss;
    private final JavaPlugin plugin;
    private final int maxDistance;

    public BossChunkListener(JavaPlugin plugin, Boss boss, int maxDistance) {
        this.boss = boss;
        this.plugin = plugin;
        this.maxDistance = maxDistance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (boss == null || boss.getEntity() == null) {
            return;
        }
        Location playerLocation = player.getLocation();
        Location bossLocation = boss.getEntity().getLocation();
        if (bossLocation == null) {
            return;
        }

        if (!playerLocation.getWorld().equals(bossLocation.getWorld())) {
            return;
        }

        double distance = playerLocation.distance(bossLocation);

        if (distance > maxDistance) {
            BossChunkLoader.loadChunksAroundBoss(plugin, boss);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        if (boss == null || boss.getEntity() == null) {
            return;
        }
        Location bossLocation = boss.getEntity().getLocation();
        if (bossLocation == null) {
            return;
        }

        if (chunk.getX() == bossLocation.getChunk().getX() && chunk.getZ() == bossLocation.getChunk().getZ()) {
            BossChunkLoader.loadChunksAroundBoss(plugin, boss);
            chunk.addPluginChunkTicket(plugin);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (boss == null || boss.getEntity() == null) {
            return;
        }
        if (event.getEntity().equals(boss.getEntity())) {
            event.setCancelled(true);
        }
    }
}
