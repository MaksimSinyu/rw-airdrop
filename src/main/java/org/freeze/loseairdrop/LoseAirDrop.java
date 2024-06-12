package org.freeze.loseairdrop;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.freeze.loseairdrop.commands.LoseAirCommand;
import org.freeze.loseairdrop.flags.CustomFlags;
import org.freeze.loseairdrop.hooks.DecentHologramsHook;
import org.freeze.loseairdrop.hooks.PlaceholderAPIHook;
import org.freeze.loseairdrop.hooks.WorldGuardHook;
import org.freeze.loseairdrop.listeners.GlobalListener;
import org.freeze.loseairdrop.listeners.airdropInteract.AirDropClickListener;
import org.freeze.loseairdrop.listeners.boss.transform.ZombieTransformListener;
import org.freeze.loseairdrop.listeners.inventory.InventoryListener;
import org.freeze.loseairdrop.listeners.player.PlayerEventsListener;
import org.freeze.loseairdrop.listeners.region.PlayerFlightListener;
import org.freeze.loseairdrop.managers.airdrop.AirDropManager;
import org.freeze.loseairdrop.managers.bossbar.BossDisplayManager;
import org.freeze.loseairdrop.managers.config.ConfigManager;
import org.freeze.loseairdrop.managers.database.DatabaseManager;
import org.freeze.loseairdrop.managers.message.MessageManager;
import org.freeze.loseairdrop.schematic.SchematicSpawner;
import org.freeze.loseairdrop.tasks.AirDropTask;

public final class LoseAirDrop extends JavaPlugin implements Listener {
    @Getter
    private static LoseAirDrop INSTANCE;
    @Getter
    private static ConfigManager configManager;
    private WorldGuardHook worldGuardHook;
    private PlaceholderAPIHook placeholderAPIHook;
    private DecentHologramsHook decentHologramsHook;
    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private AirDropManager airDropManager;
    @Getter
    private BossDisplayManager bossDisplayManager;
    @Getter
    private MessageManager messageManager;

    private AirDropTask airDropTask;

    @Override
    public void onLoad() {
        registerFlags();
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        World world = Bukkit.getWorld("world");
        if (world == null) {
            getLogger().severe("Мир 'world' не найден! Плагин не будет работать корректно.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        configManager = ConfigManager.getInstance(this);
        databaseManager = new DatabaseManager(this);
        messageManager = new MessageManager(configManager, databaseManager);

        getServer().getPluginManager().registerEvents(this, this);

        this.airDropManager = new AirDropManager(this, configManager, messageManager, decentHologramsHook);
        airDropTask = new AirDropTask(this, airDropManager);
        airDropTask.scheduleTask(216000L);
        this.bossDisplayManager = BossDisplayManager.getInstance(this, configManager, this.airDropManager, databaseManager, airDropTask); // Correctly initialize the singleton instance

        airDropManager.setBossDisplayManager(bossDisplayManager);

        getServer().getPluginManager().registerEvents(new AirDropClickListener(bossDisplayManager, this), this);
        getDataFolder().mkdirs();
        getServer().getPluginManager().registerEvents(new GlobalListener(this.airDropManager), this);
        getServer().getPluginManager().registerEvents(new PlayerFlightListener(this), this);
        getServer().getPluginManager().registerEvents(new ZombieTransformListener(), this);

        getCommand("loseair").setExecutor(new LoseAirCommand(airDropManager, databaseManager, configManager, bossDisplayManager, this));

        getServer().getPluginManager().registerEvents(new InventoryListener(databaseManager), this);
        getServer().getPluginManager().registerEvents(new PlayerEventsListener(this, airDropManager), this);

        SchematicSpawner.INSTANCE = new SchematicSpawner(this);

        worldGuardHook = new WorldGuardHook();
        if (!worldGuardHook.setupWorldGuard()) {
            getLogger().warning("WorldGuard не найден или не загружен!");
        }

        placeholderAPIHook = new PlaceholderAPIHook();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("PlaceholderAPI не найден или не загружен!");
        }
        initializeDecentHologramsHook();
    }

    private void initializeDecentHologramsHook() {
        Plugin decentHologramsPlugin = Bukkit.getPluginManager().getPlugin("DecentHolograms");
        if (decentHologramsPlugin != null && decentHologramsPlugin.isEnabled()) {
            getLogger().info("DecentHolograms found and enabled, initializing DecentHologramsHook...");
            decentHologramsHook = new DecentHologramsHook();
        } else {
            getLogger().warning("DecentHolograms not found or not enabled, will wait for PluginEnableEvent...");
            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onPluginEnable(PluginEnableEvent event) {
                    if (event.getPlugin().getName().equals("DecentHolograms")) {
                        getLogger().info("DecentHolograms plugin enabled, initializing DecentHologramsHook...");
                        decentHologramsHook = new DecentHologramsHook();
                        PluginEnableEvent.getHandlerList().unregister(this);
                    }
                }
            }, this);
        }
    }

    public static void resetAirDropTask() {
        if (INSTANCE != null) {
            INSTANCE.airDropTask.cancelTask();
            INSTANCE.airDropTask.scheduleTask(216000L);
        }
    }

    private void registerFlags() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            registry.register(CustomFlags.NO_FLY);
            getLogger().info("Custom flag no-fly registered successfully.");
        } catch (Exception e) {
            getLogger().warning("Failed to register custom flags: " + e.getMessage());
        }
    }
}
