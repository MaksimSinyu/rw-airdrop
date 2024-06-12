package org.freeze.loseairdrop.airdrop.types;

import org.freeze.loseairdrop.LoseAirDrop;
import org.freeze.loseairdrop.airdrop.AirDrop;

public class MythicalAirDrop extends AirDrop {
    public MythicalAirDrop() {
        super(LoseAirDrop.getConfigManager().getBossbarTitle("mythical"));
    }
}