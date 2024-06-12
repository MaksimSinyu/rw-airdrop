package org.freeze.loseairdrop.schematic;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;

public class SchematicSpawner {
    private static JavaPlugin plugin;

    @Getter
    public static SchematicSpawner INSTANCE;

    private Clipboard clipboard;
    private BlockVector3 pasteLocation;

    public SchematicSpawner(JavaPlugin plugin) {
        SchematicSpawner.plugin = plugin;
        INSTANCE = this;
    }

    public void pasteSchematic(Location loc, String schematicFileName) {
        File schematicFile = new File(plugin.getDataFolder(), schematicFileName);
        if (!schematicFile.exists()) {
            plugin.getLogger().warning("Schematic file not found: " + schematicFileName);
            return;
        }

        try (FileInputStream fis = new FileInputStream(schematicFile)) {
            ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
            if (format == null) {
                plugin.getLogger().warning("Unsupported schematic format for file: " + schematicFileName);
                return;
            }
            try (ClipboardReader reader = format.getReader(fis)) {
                com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(loc.getWorld());
                try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, -1)) {
                    clipboard = reader.read(); // Сохраняем схему в поле
                    ClipboardHolder holder = new ClipboardHolder(clipboard);
                    pasteLocation = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
                    Operation operation = holder.createPaste(editSession)
                            .to(pasteLocation)
                            .ignoreAirBlocks(false)
                            .build();
                    Operations.complete(operation);
                    editSession.flushSession();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().warning("Failed to paste schematic: " + e.getMessage());
        }
    }

    public void removeSchematic() {
        if (clipboard == null || pasteLocation == null) {
            plugin.getLogger().warning("No clipboard or paste location to remove schematic from.");
            return;
        }

        try {
            com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(plugin.getServer().getWorlds().get(0));
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, -1)) {

                BlockVector3 start = pasteLocation.add(clipboard.getMinimumPoint().subtract(clipboard.getOrigin()));

                BlockVector3 end = pasteLocation.add(clipboard.getMaximumPoint().subtract(clipboard.getOrigin()));

                Region region = new CuboidRegion(start, end);

                editSession.setBlocks(region, BlockTypes.AIR.getDefaultState());
                editSession.flushSession();
            }
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().warning("Failed to remove schematic: " + e.getMessage());
        }
    }
}
