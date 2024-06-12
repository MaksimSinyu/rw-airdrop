package org.freeze.loseairdrop.hooks;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.UUID;

public class DecentHologramsHook {

    public boolean setupDecentHolograms() {
        Plugin decentHolograms = Bukkit.getServer().getPluginManager().getPlugin("DecentHolograms");
        if (decentHolograms != null && decentHolograms.isEnabled()) {
            Bukkit.getLogger().info("DecentHolograms успешно загружен!");
            try {
                DecentHologramsAPI.get();
                Bukkit.getLogger().info("DecentHolograms API успешно инициализировано!");
                return true;
            } catch (IllegalStateException e) {
                Bukkit.getLogger().severe("DecentHolograms API не инициализировано! Причина: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                Bukkit.getLogger().severe("Неизвестная ошибка при инициализации DecentHolograms API: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Bukkit.getLogger().warning("DecentHolograms не найден или не загружен!");
        }
        return false;
    }

    public static void removeHologram(Hologram hologram) {
        try {
            DHAPI.removeHologram(hologram.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Hologram createHologram(JavaPlugin plugin, Location location, String... lines) {
        String hologramName = "hologram_" + UUID.randomUUID();
        try {
            Hologram hologram = DHAPI.createHologram(hologramName, location);
            Arrays.stream(lines).forEach(line -> DHAPI.addHologramLine(hologram, line));
            Bukkit.getLogger().info("Голограмма успешно создана: " + hologramName);
            return hologram;
        } catch (Exception e) {
            Bukkit.getLogger().severe("Ошибка при создании голограммы: " + hologramName + ", причина: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
