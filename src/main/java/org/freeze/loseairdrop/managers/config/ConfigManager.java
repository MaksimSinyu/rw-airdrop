package org.freeze.loseairdrop.managers.config;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private static ConfigManager instance;
    private final JavaPlugin plugin;
    private FileConfiguration config;
    @Getter
    private Map<String, String> airDropTypeMappings;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
        reloadConfig();
    }

    public static ConfigManager getInstance(JavaPlugin plugin) {
        if (instance == null) {
            instance = new ConfigManager(plugin);
        }
        return instance;
    }
    public List<String> getHelpMessages() {
        return config.getStringList("commands.helpMessage");
    }

    public String getSchematicName() {
        return config.getString("airdrop.schematic.name");
    }

    public String getHologramSetting(String setting) {
        return config.getString("airdrop.hologram." + setting);
    }

    public String getBossbarTitle(String setting) {
        return config.getString("airdrop.types." + setting);
    }

    public int getOpeningDelay() {
        return config.getInt("airdrop.openingDelay");
    }

    public String getBossBarTitleBoss(String setting) {
        return config.getString("bossBar." + setting);
    }

    public String getBossName(String type) {
        return ChatColor.translateAlternateColorCodes('&', config.getString(type + ".name", "&fBoss"));
    }

    public int getBossHealth(String type) {
        return config.getInt(type + ".hp", 100);
    }

    public List<String> getBlockedCommands() {
        return config.getStringList("airdrop.region.block-cmds");
    }

    public int getRegionSize() {
        return config.getInt("airdrop.region.region-blocks-size");
    }

    public boolean isValidAirDropType(String type) {
        return config.contains("airdrop.types." + type);
    }

    public List<String> getHelpMessage() {
        return config.getStringList("commands.helpMessage");
    }

    public List<String> getOpenMessage() {
        return config.getStringList("commands.openMessage");
    }

    public List<String> getReloadMessage() {
        return config.getStringList("commands.reloadMessage");
    }

    public List<String> getSpawnMessage() {
        return config.getStringList("commands.spawnMessage");
    }
    public String getNoPermissionMessage() {
        return config.getString("commands.hasntPermission", "&cYou do not have permission to use this command.");
    }

    public String getRemoveSuccessMessage(Boolean success) {
        return success ? config.getString("commands.successRemove") : config.getString("commands.noActiveAirdrop");
    }
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        loadAirDropTypeMappings();
    }

    private void loadAirDropTypeMappings() {
        Bukkit.getLogger().info("load air is");
        airDropTypeMappings = new HashMap<>();
        for (String key : config.getConfigurationSection("airdrop.types").getKeys(false)) {
            String value = config.getString("airdrop.types." + key);
            Bukkit.getLogger().info("VALUE IS " + value);
            airDropTypeMappings.put(value, key);
        }
    }

    public List<String> getAirDropSpawnMessages() {
        return config.getStringList("airdrop.spawnMessage");
    }

    public String getAirDropTypeByDisplayName(String displayName) {
        Bukkit.getLogger().info("DISPLAY NAME IS " + displayName);
        Bukkit.getLogger().info("air drop type mappings " + airDropTypeMappings);
        return airDropTypeMappings.get(displayName);
    }
}
