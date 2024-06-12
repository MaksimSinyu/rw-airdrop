package org.freeze.loseairdrop.bosses.chunk;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.freeze.loseairdrop.bosses.Boss;

public class BossChunkLoader {

    public static void loadChunksAroundBoss(JavaPlugin plugin, Boss boss) {
        if (boss.getEntity() != null && boss.getEntity().isValid()) {
            Location location = boss.getEntity().getLocation();
            int chunkX = location.getChunk().getX();
            int chunkZ = location.getChunk().getZ();
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Chunk chunk = location.getWorld().getChunkAt(chunkX + x, chunkZ + z);
                    if (!chunk.isLoaded()) {
                        chunk.load(true);
                    }
                    chunk.addPluginChunkTicket(plugin);
                }
            }
        }
    }
}