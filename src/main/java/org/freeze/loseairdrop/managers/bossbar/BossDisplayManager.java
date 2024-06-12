package org.freeze.loseairdrop.managers.bossbar;

import eu.decentsoftware.holograms.api.DHAPI;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.freeze.loseairdrop.LoseAirDrop;
import org.freeze.loseairdrop.airdrop.AirDrop;
import org.freeze.loseairdrop.airdrop.dto.AirDropItem;
import org.freeze.loseairdrop.airdrop.states.AirDropState;
import org.freeze.loseairdrop.airdrop.strategy.AirDropStateStrategy;
import org.freeze.loseairdrop.airdrop.strategy.BossFightStrategy;
import org.freeze.loseairdrop.airdrop.strategy.WaitingTimeStrategy;
import org.freeze.loseairdrop.bosses.Boss;
import org.freeze.loseairdrop.hooks.DecentHologramsHook;
import org.freeze.loseairdrop.managers.airdrop.AirDropManager;
import org.freeze.loseairdrop.managers.config.ConfigManager;
import org.freeze.loseairdrop.managers.database.DatabaseManager;
import org.freeze.loseairdrop.managers.message.MessageManager;
import org.freeze.loseairdrop.managers.region.AirDropRegionManager;
import org.freeze.loseairdrop.schematic.SchematicSpawner;
import org.freeze.loseairdrop.tasks.AirDropTask;
import eu.decentsoftware.holograms.api.holograms.Hologram;

import java.util.*;

@Getter
public class BossDisplayManager {
    private static BossDisplayManager instance;
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private Hologram activeHologram;
    private BossBar activeBossBar;
    private Boss currentBoss;
    private AirDropState currentState;
    private final AirDropManager airDropManager;
    private final AirDropTask airDropTask;
    private Location airDropLocation;
    private BukkitTask updateTask = null;
    private final DatabaseManager databaseManager;
    private final Map<AirDropState, AirDropStateStrategy> strategies = new HashMap<>();

    public BossDisplayManager(JavaPlugin plugin, ConfigManager configManager, AirDropManager airDropManager, DatabaseManager databaseManager, AirDropTask airDropTask) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.airDropManager = airDropManager;
        this.databaseManager = databaseManager;
        this.airDropTask = airDropTask;
        initStrategies();
        instance = this;
    }

    public static synchronized BossDisplayManager getInstance(JavaPlugin plugin, ConfigManager configManager, AirDropManager airDropManager, DatabaseManager databaseManager, AirDropTask airDropTask) {
        if (instance == null) {
            instance = new BossDisplayManager(plugin, configManager, airDropManager, databaseManager, airDropTask);
        }
        return instance;
    }

    public static BossDisplayManager getInstance() {
        return instance;
    }

    private void initStrategies() {
        strategies.put(AirDropState.WAITING_TIME, new WaitingTimeStrategy());
        strategies.put(AirDropState.BOSS_FIGHT, new BossFightStrategy());
    }

    public boolean isBoss(Zombie zombie) {
        return currentBoss != null && currentBoss.getEntity() instanceof Zombie && currentBoss.getEntity().getUniqueId().equals(zombie.getUniqueId());
    }

    public void updateHologramAndBossBar(Boss boss, AirDropState state) {
        this.currentBoss = boss;
        this.currentState = state;
        if (state != null && strategies.containsKey(state)) {
            strategies.get(state).execute(this);
        }
    }

    public void killCurrentBoss() {
        if (currentBoss != null && currentBoss.getEntity() != null && !currentBoss.getEntity().isDead()) {
            currentBoss.getEntity().setHealth(0);
            airDropManager.updateCountdown(configManager.getOpeningDelay());
            startUpdateTask();
        } else {
            Bukkit.getLogger().warning("Нет активного босса для убийства.");
        }
    }

    public void startUpdateTask() {
        if (updateTask != null && !updateTask.isCancelled()) {
            updateTask.cancel();
        }

        updateTask = new BukkitRunnable() {
            private int countdown = configManager.getOpeningDelay();
            private boolean hasExecutedCountdownLogic = false;

            @Override
            public void run() {
                Bukkit.getLogger().info("Update Task Running - Current State: " + currentState + ", Countdown: " + countdown);

                if (currentState == AirDropState.OPENED) {
                    Bukkit.getLogger().info("State is OPENED, cancelling task.");
                    this.cancel();
                    return;
                }

                if (currentState == AirDropState.WAITING_TIME) {
                    countdown--;
                    airDropManager.updateCountdown(countdown);
                    Bukkit.getLogger().info("State is WAITING_TIME, updated countdown: " + countdown);

                    if (countdown <= 0 && !hasExecutedCountdownLogic) {
                        currentState = AirDropState.OPENED;
                        Bukkit.getLogger().info("Countdown reached zero, state set to OPENED");

                        Location hologramLocation = activeHologram != null ? activeHologram.getLocation() : null;
                        if (hologramLocation != null) {
                            hologramLocation.getWorld().strikeLightningEffect(hologramLocation);
                            DecentHologramsHook.removeHologram(activeHologram);
                        }
                        activeHologram = null;

                        Block nearestRespawnAnchor = AirDrop.findNearestBlock(airDropLocation, Material.RESPAWN_ANCHOR, 5);
                        Bukkit.getLogger().info("nearest respawn anchor is " + nearestRespawnAnchor.getLocation());
                        if (nearestRespawnAnchor != null) {
                            Location anchorLocation = nearestRespawnAnchor.getLocation();
                            Bukkit.getLogger().info("Nearest Respawn Anchor found at: " + anchorLocation);

                            anchorLocation.getWorld().strikeLightning(anchorLocation);
                            updateBossBar(currentBoss, AirDropState.OPENED, anchorLocation);

                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                if (activeBossBar != null) {
                                    activeBossBar.removeAll();
                                    activeBossBar = null;
                                }
                            }, 120 * 20L);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Bukkit.getLogger().info("Executing explosion at: " + anchorLocation);

                                    anchorLocation.getWorld().createExplosion(anchorLocation, 16F, false, false);
                                    anchorLocation.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, anchorLocation, 10);
                                    anchorLocation.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, anchorLocation, 30);
                                    anchorLocation.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, anchorLocation, 50);

                                    double radius = 5.0;
                                    for (Player player : anchorLocation.getWorld().getPlayers()) {
                                        if (player.getLocation().distance(anchorLocation) <= radius) {
                                            Vector knockbackDirection = player.getLocation().toVector().subtract(anchorLocation.toVector()).normalize().multiply(5);
                                            player.setVelocity(knockbackDirection);
                                            Bukkit.getLogger().info("Knocking back player: " + player.getName());
                                        }
                                    }

                                    anchorLocation.getWorld().playSound(anchorLocation, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                                    launchItemsUpwards(anchorLocation);
                                    SchematicSpawner.getINSTANCE().removeSchematic();
                                    removeAirDrop();
                                }
                            }.runTaskLater(plugin, 60L);

                            hasExecutedCountdownLogic = true;
                            this.cancel();
                            return;
                        }
                    }
                }

                Location loc = currentBoss != null && currentBoss.getEntity() != null ? currentBoss.getEntity().getLocation() : null;
                Block nearestRespawnAnchor = AirDrop.findNearestBlock(loc, Material.RESPAWN_ANCHOR, 5);
                Location hologramLocation = nearestRespawnAnchor != null ? nearestRespawnAnchor.getLocation().add(0.5, 2.0, 0.5) : null;
                updateHologram(hologramLocation, currentBoss);
                updateBossBar(currentBoss, currentState, airDropLocation);
            }

        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void launchItemsUpwards(Location location) {
        MessageManager messageManager = LoseAirDrop.getINSTANCE().getMessageManager();
        List<AirDropItem> selectedItems = messageManager.getSelectedItems();

        if (selectedItems == null || selectedItems.isEmpty()) {
            Bukkit.getLogger().warning("No items selected for AirDrop.");
            return;
        }

        Random random = new Random();
        Bukkit.getLogger().info("Launching items upwards from location: " + location);

        for (AirDropItem item : selectedItems) {
            ItemStack itemStack = item.toItemStack();
            int totalAmount = itemStack.getAmount();

            Bukkit.getLogger().info("Dropping item: " + itemStack.getType() + " Amount: " + totalAmount);

            for (int i = 0; i < totalAmount; i++) {
                ItemStack singleItemStack = new ItemStack(itemStack.getType(), 1);
                if (itemStack.getItemMeta() != null) {
                    ItemMeta meta = itemStack.getItemMeta();
                    singleItemStack.setItemMeta(meta);
                }

                singleItemStack = addUniqueTag(singleItemStack);

                double xOffset = (random.nextDouble() - 0.5) * 0.5;
                double yOffset = (random.nextDouble() - 0.5) * 0.5;
                double zOffset = (random.nextDouble() - 0.5) * 0.5;

                Location dropLocation = location.clone().add(xOffset, yOffset, zOffset);
                Bukkit.getLogger().info("Dropping item at location: " + dropLocation);

                Item droppedItem = dropLocation.getWorld().dropItem(dropLocation, singleItemStack);
                droppedItem.setGlowing(true);
                droppedItem.setPickupDelay(60);

                double x = random.nextDouble() * 2.0 - 1.0;
                double y = random.nextDouble() * 2.0 + 1.0;
                double z = random.nextDouble() * 2.0 - 1.0;
                droppedItem.setVelocity(new Vector(x, y, z));

                Bukkit.getLogger().info("Dropped item with velocity: " + droppedItem.getVelocity());
            }
        }
    }

    private ItemStack addUniqueTag(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, "unique_id");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, UUID.randomUUID().toString());
        item.setItemMeta(meta);
        return item;
    }

    private void updateHologram(Location loc, Boss boss) {
        Bukkit.getLogger().info("UPDATE HOLOGRAM CURRENT STATE IS " + currentState);
        String additionalText = "";
        String displayName = airDropManager.getActiveAirDrop().getType();
        String airdropClosed = ChatColor.translateAlternateColorCodes('&', configManager.getHologramSetting("airdropClosed"))
                .replace("{airdropType}", displayName);

        switch (currentState) {
            case BOSS_FIGHT:
                Bukkit.getLogger().info("STATE BOSS FIGHT");
                additionalText = ChatColor.translateAlternateColorCodes('&', configManager.getHologramSetting("killBoss"));
                break;
            case WAITING_TIME:
                additionalText = ChatColor.translateAlternateColorCodes('&', configManager.getHologramSetting("killedBoss"))
                        .replace("{seconds}", String.valueOf(airDropManager.getCountdown()));
                Bukkit.getLogger().info("killed boss 2 text is " + additionalText);

                if (activeHologram != null) {
                    DHAPI.setHologramLine(activeHologram.getPage(0).getLine(1), additionalText);
                    return;
                }
                break;
        }

        if (loc != null && activeHologram == null) {
            Hologram newHologram = DecentHologramsHook.createHologram(plugin, loc, airdropClosed);
            DHAPI.addHologramLine(newHologram, additionalText);
            activeHologram = newHologram;
        } else if (loc != null && activeHologram != null) {
            DHAPI.setHologramLine(activeHologram.getPage(0).getLine(0), airdropClosed);
            DHAPI.setHologramLine(activeHologram.getPage(0).getLine(1), additionalText);
        }
    }

    public void updateBossBar(Boss boss, AirDropState state, Location loc) {
        Bukkit.getLogger().info("updated boss bar is " + airDropManager.getCountdown());
        String bossBarTitle = selectBossBarTitle(boss, state, loc);
        Bukkit.getLogger().info("BOSS BAR TITLE (before color translation): " + bossBarTitle);
        bossBarTitle = ChatColor.translateAlternateColorCodes('&', bossBarTitle);
        Bukkit.getLogger().info("BOSS BAR TITLE (after color translation): " + bossBarTitle);

        if (activeBossBar != null) {
            activeBossBar.setTitle(bossBarTitle);
            if (state == AirDropState.WAITING_ACTIVATION || state == AirDropState.BOSS_FIGHT) {
                activeBossBar.setProgress(boss.getEntity().getHealth() / boss.getEntity().getMaxHealth());
                activeBossBar.setColor(BarColor.RED);
            } else if (state == AirDropState.WAITING_TIME) {
                double progress = (double) airDropManager.getCountdown() / configManager.getOpeningDelay();
                activeBossBar.setProgress(progress);
                activeBossBar.setColor(BarColor.YELLOW);
            } else if (state == AirDropState.OPENED) {
                activeBossBar.setProgress(1.0);
                activeBossBar.setColor(BarColor.GREEN);
            }
        } else {
            BarColor barColor = state == AirDropState.WAITING_TIME ? BarColor.YELLOW : BarColor.RED;
            BossBar newBossBar = Bukkit.createBossBar(bossBarTitle, barColor, BarStyle.SOLID);

            if (state == AirDropState.WAITING_TIME) {
                double progress = (double) airDropManager.getCountdown() / configManager.getOpeningDelay();
                newBossBar.setProgress(progress);
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                newBossBar.addPlayer(player);
            }

            activeBossBar = newBossBar;
        }
    }

    private String selectBossBarTitle(Boss boss, AirDropState state, Location loc) {
        switch (state) {
            case WAITING_ACTIVATION:
            case BOSS_FIGHT:
                return formatBossbarFight(boss, "titleBoss", loc);
            case WAITING_TIME:
                return formatBossBarWaiting(boss, "titleClicked", loc);
            case OPENED:
                return formatBossBarOpened(boss, "titleOpened", airDropLocation);
            default:
                return "Unknown State";
        }
    }

    private String formatBossbarFight(Boss boss, String titleTemplateKey, Location loc) {
        String type = airDropManager.getActiveAirDrop().getType();

        int x_cords = airDropLocation != null ? airDropLocation.getBlockX() : 0;
        int z_cords = airDropLocation != null ? airDropLocation.getBlockZ() : 0;

        String titleTemplate = configManager.getBossBarTitleBoss(titleTemplateKey);
        if (titleTemplate == null) {
            Bukkit.getLogger().warning("Boss bar title configuration is missing.");
            return "Configuration Error";
        }

        return ChatColor.translateAlternateColorCodes('&', titleTemplate)
                .replace("{type}", type)
                .replace("{x_cords}", String.valueOf(x_cords))
                .replace("{z_cords}", String.valueOf(z_cords));
    }

    private String formatBossBarWaiting(Boss boss, String titleTemplateKey, Location loc) {
        int openingDelay = airDropManager.getCountdown();
        String type = airDropManager.getActiveAirDrop().getType();
        int x = airDropLocation != null ? airDropLocation.getBlockX() : 0;
        int z = airDropLocation != null ? airDropLocation.getBlockZ() : 0;

        String titleTemplate = configManager.getBossBarTitleBoss(titleTemplateKey);
        if (titleTemplate == null) {
            Bukkit.getLogger().warning("Boss bar title configuration is missing.");
            return "Configuration Error";
        }

        return ChatColor.translateAlternateColorCodes('&', titleTemplate)
                .replace("{type}", type)
                .replace("{seconds}", String.valueOf(openingDelay))
                .replace("{x_cord}", String.valueOf(x))
                .replace("{z_cord}", String.valueOf(z));
    }

    private String formatBossBarOpened(Boss boss, String titleTemplateKey, Location loc) {
        if (boss == null || boss.getEntity() == null) {
            Bukkit.getLogger().warning("Boss or boss entity is null.");
            return "Invalid Boss Data";
        }

        String type = airDropManager.getActiveAirDrop().getType();

        int x_cords = airDropLocation != null ? airDropLocation.getBlockX() : 0;
        int z_cords = airDropLocation != null ? airDropLocation.getBlockZ() : 0;

        String titleTemplate = configManager.getBossBarTitleBoss(titleTemplateKey);
        if (titleTemplate == null) {
            Bukkit.getLogger().warning("Boss bar title configuration is missing.");
            return "Configuration Error";
        }

        return ChatColor.translateAlternateColorCodes('&', titleTemplate)
                .replace("{type}", type)
                .replace("{x_cords}", String.valueOf(x_cords))
                .replace("{z_cords}", String.valueOf(z_cords));
    }

    public void setAirDropLocation(Location location) {
        this.airDropLocation = location;
    }

    public boolean removeAirDrop() {
        if (airDropManager.getActiveAirDrop() == null) {
            Bukkit.getLogger().warning("No active airdrop to remove.");
            return false;
        }

        if (activeHologram != null) {
            DecentHologramsHook.removeHologram(activeHologram);
            activeHologram = null;
        }

        if (airDropManager.getActiveHologram() != null) {
            DecentHologramsHook.removeHologram(airDropManager.getActiveHologram());
            airDropManager.setActiveHologram(null);
        }

        if (currentBoss != null && currentBoss.getEntity() != null && !currentBoss.getEntity().isDead()) {
            currentBoss.getEntity().setHealth(0);
            currentBoss.onDeath();
            currentBoss.getEntity().remove();
            currentBoss = null;
        }

        if (currentState != AirDropState.OPENED && activeBossBar != null) {
            activeBossBar.removeAll();
            activeBossBar = null;
        }

        String regionId = airDropManager.getRegionId();

        Bukkit.getLogger().info("region id is " + regionId);

        if (regionId != null) {
            Bukkit.getLogger().info("Removing protected region with ID: " + regionId);
            AirDropRegionManager.removeProtectedRegionById(regionId);
        }

        SchematicSpawner.getINSTANCE().removeSchematic();

        airDropManager.updateCountdown(configManager.getOpeningDelay());

        if (updateTask != null && !updateTask.isCancelled()) {
            updateTask.cancel();
            updateTask = null;
        }

        airDropManager.setActiveAirDrop(null);
        airDropTask.scheduleTask(216000L);
        currentState = null;

        return true;
    }
}
