package org.freeze.loseairdrop.airdrop;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.freeze.loseairdrop.airdrop.types.CommonAirDrop;
import org.freeze.loseairdrop.airdrop.types.LegendaryAirDrop;
import org.freeze.loseairdrop.airdrop.types.MythicalAirDrop;
import org.freeze.loseairdrop.airdrop.types.RareAirDrop;

import java.util.Random;

public abstract class AirDrop {
    @Getter
    protected String type;

    public AirDrop(String type) {
        this.type = type;
    }

    public static AirDrop createRandomAirDrop() {
        Random random = new Random();
        int choice = random.nextInt(4);
        switch (choice) {
            case 0:
                return new LegendaryAirDrop();
            case 1:
                return new MythicalAirDrop();
            case 2:
                return new RareAirDrop();
            case 3:
                return new CommonAirDrop();
            default:
                return new CommonAirDrop();
        }
    }
    public static Block findNearestBlock(Location center, Material material, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    Block block = center.getWorld().getBlockAt(center.getBlockX() + dx, center.getBlockY() + dy, center.getBlockZ() + dz);
                    if (block.getType() == material) {
                        return block;
                    }
                }
            }
        }
        return null;
    }
}
