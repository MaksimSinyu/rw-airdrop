package org.freeze.loseairdrop.managers.airdrop;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.freeze.loseairdrop.airdrop.AirDrop;
import org.freeze.loseairdrop.airdrop.states.AirDropState;
import org.freeze.loseairdrop.airdrop.types.CommonAirDrop;
import org.freeze.loseairdrop.airdrop.types.LegendaryAirDrop;
import org.freeze.loseairdrop.airdrop.types.MythicalAirDrop;
import org.freeze.loseairdrop.airdrop.types.RareAirDrop;
import org.freeze.loseairdrop.bosses.Boss;
import org.freeze.loseairdrop.bosses.factory.BossFactory;
import org.freeze.loseairdrop.hooks.DecentHologramsHook;
import org.freeze.loseairdrop.hooks.PlaceholderAPIHook;
import org.freeze.loseairdrop.managers.bossbar.BossDisplayManager;
import org.freeze.loseairdrop.managers.config.ConfigManager;
import org.freeze.loseairdrop.managers.message.MessageManager;
import org.freeze.loseairdrop.managers.region.AirDropRegionManager;
import org.freeze.loseairdrop.schematic.SchematicSpawner;

import java.util.*;

import static org.freeze.loseairdrop.airdrop.states.AirDropState.WAITING_ACTIVATION;

@Setter
@Getter
public class AirDropManager {

    private static AirDropManager instance;
    private AirDrop activeAirDrop;
    private BossBar activeBossBar;
    private Hologram activeHologram;
    private AirDropState airDropState;
    protected final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final Map<UUID, BossBar> playerBossBars = new HashMap<>();
    private BossDisplayManager bossDisplayManager;
    private PlaceholderAPIHook placeholderAPIHook = new PlaceholderAPIHook();
    private DecentHologramsHook decentHologramsHook;
    private int countdown;
    private String regionId;

    public AirDropManager(JavaPlugin plugin, ConfigManager configManager, MessageManager messageManager, DecentHologramsHook decentHologramsHook) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageManager = messageManager;
        this.decentHologramsHook = decentHologramsHook;
    }

    public void setBossDisplayManager(BossDisplayManager bossDisplayManager) {
        this.bossDisplayManager = bossDisplayManager;
    }

    public void updateCountdown(int countdown) {
        this.countdown = countdown;
    }

    public boolean hasActiveAirDrop() {
        return activeAirDrop != null;
    }

    public AirDrop createRandomAirDrop(World world, int x, int y, int z) {
        AirDrop airDrop;
        switch (new Random().nextInt(4)) {
            case 0:
                airDrop = new LegendaryAirDrop();
                break;
            case 1:
                airDrop = new MythicalAirDrop();
                break;
            case 2:
                airDrop = new RareAirDrop();
                break;
            default:
                airDrop = new CommonAirDrop();
        }
        Location loc = new Location(world, x, y, z);
        spawnAirDropAtLocation(airDrop, loc);
        return airDrop;
    }

    public void spawnAirDropAtLocation(AirDrop airDrop, Location loc) {
        SchematicSpawner.getINSTANCE().pasteSchematic(loc, configManager.getSchematicName());
        this.regionId = AirDropRegionManager.createProtectedRegion(loc);

        Bukkit.getLogger().info("REGION ID IS " + regionId);
        activeAirDrop = airDrop;
        airDropState = WAITING_ACTIVATION;

        messageManager.broadcastAirDropSpawnMessage(airDrop);

        Block nearestRespawnAnchor = AirDrop.findNearestBlock(loc, Material.RESPAWN_ANCHOR, 5);
        if (nearestRespawnAnchor != null) {
            Location hologramLocation = nearestRespawnAnchor.getLocation().add(0.5, 2.0, 0.5);
            if (activeHologram == null) {
                String hologramText = configManager.getHologramSetting("beforeOpen");
                List<String> hologramLines = Arrays.asList(hologramText.split("\n"));
                activeHologram = createDecentHologram(hologramLocation, "airdrop_hologram", hologramLines);
            }
        }
        bossDisplayManager.setAirDropLocation(loc);
        bossDisplayManager.updateBossBar(null, airDropState, loc);
    }


    private Hologram createDecentHologram(Location location, String name, List<String> hologramLines) {
        String hologramName = name + "_" + System.currentTimeMillis();
        try {
            return DHAPI.createHologram(hologramName, location, true, hologramLines);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void activateAirDrop(Location loc, Player player) {
        if (activeAirDrop != null) {
            if (activeHologram != null) {
                DecentHologramsHook.removeHologram(activeHologram);
                activeHologram = null;
            }
            Boss boss = spawnBossForAirDrop(activeAirDrop, loc, player);
            airDropState = AirDropState.BOSS_FIGHT;
            bossDisplayManager.updateHologramAndBossBar(boss, airDropState);
            bossDisplayManager.updateBossBar(boss, airDropState, loc);
        } else {
            Bukkit.getLogger().warning("No active air drop to activate.");
        }
    }


    private Boss spawnBossForAirDrop(AirDrop airDrop, Location loc, Player player) {
        World world = loc.getWorld();
        Boss boss = BossFactory.createBoss(plugin, "poseidon", world, loc, configManager);
        boss.spawn(world, loc);
        return boss;
    }

}