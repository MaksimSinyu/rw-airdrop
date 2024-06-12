package org.freeze.loseairdrop.managers.region;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.freeze.loseairdrop.LoseAirDrop;
import org.freeze.loseairdrop.flags.CustomFlags;

import java.util.HashSet;
import java.util.List;

public class AirDropRegionManager {

    public static String createProtectedRegion(Location center) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(center.getWorld()));

        if (regions == null) {
            Bukkit.getLogger().warning("RegionManager is null for world: " + center.getWorld().getName());
            return null;
        }

        int radius = LoseAirDrop.getConfigManager().getRegionSize();
        int minX = center.getBlockX() - radius;
        int maxX = center.getBlockX() + radius;
        int minZ = center.getBlockZ() - radius;
        int maxZ = center.getBlockZ() + radius;
        int minY = center.getWorld().getMinHeight();
        int maxY = center.getWorld().getMaxHeight();

        BlockVector3 min = BlockVector3.at(minX, minY, minZ);
        BlockVector3 max = BlockVector3.at(maxX, maxY, maxZ);

        String regionId = "airdrop_" + center.getBlockX() + "_" + center.getBlockZ();
        Bukkit.getLogger().info("CREATE PROCTED REGION IS " + regionId);
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionId, min, max);

        List<String> blockedCmds = LoseAirDrop.getConfigManager().getBlockedCommands();
        SetFlag<String> blockedCmdsFlag = (SetFlag<String>) Flags.fuzzyMatchFlag(WorldGuard.getInstance().getFlagRegistry(), "blocked-cmds");

        if (blockedCmdsFlag != null) {
            region.setFlag(blockedCmdsFlag, new HashSet<>(blockedCmds));
        }

        region.setFlag(CustomFlags.NO_FLY, StateFlag.State.DENY);

        regions.addRegion(region);

        return regionId;
    }

    public static void removeProtectedRegion(Location center) {
        if (center == null) {
            Bukkit.getLogger().warning("Center location is null!");
            return;
        }

        World world = center.getWorld();
        if (world == null) {
            Bukkit.getLogger().warning("World is null for center location: " + center);
            return;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        if (container == null) {
            Bukkit.getLogger().warning("RegionContainer is null!");
            return;
        }

        RegionManager regions = container.get(BukkitAdapter.adapt(world));
        if (regions == null) {
            Bukkit.getLogger().warning("RegionManager is null for world: " + world.getName());
            return;
        }

        String regionId = "airdrop_" + center.getBlockX() + "_" + center.getBlockZ();
        Bukkit.getLogger().info("Attempting to remove region with ID: " + regionId);

        if (regions.hasRegion(regionId)) {
            regions.removeRegion(regionId);
            Bukkit.getLogger().info("Region " + regionId + " successfully removed.");
        } else {
            Bukkit.getLogger().warning("Region with ID " + regionId + " not found.");
        }
    }

    public static void removeProtectedRegionById(String regionId) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        for (RegionManager regions : container.getLoaded()) {
            if (regions.hasRegion(regionId)) {
                regions.removeRegion(regionId);
                Bukkit.getLogger().info("Region " + regionId + " successfully removed.");
                return;
            }
        }
        Bukkit.getLogger().warning("Region with ID " + regionId + " not found in any loaded worlds.");
    }
}