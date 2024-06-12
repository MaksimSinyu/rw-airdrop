package org.freeze.loseairdrop.hooks;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class WorldGuardHook {

    private WorldGuardPlugin worldGuardPlugin;

    public boolean setupWorldGuard() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return false;
        }

        worldGuardPlugin = (WorldGuardPlugin) plugin;
        return true;
    }

    public WorldGuardPlugin getWorldGuardPlugin() {
        return worldGuardPlugin;
    }
}
