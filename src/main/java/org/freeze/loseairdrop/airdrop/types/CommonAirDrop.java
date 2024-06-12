package org.freeze.loseairdrop.airdrop.types;

import org.freeze.loseairdrop.LoseAirDrop;
import org.freeze.loseairdrop.airdrop.AirDrop;

public class CommonAirDrop extends AirDrop {
    public CommonAirDrop() {
        super(LoseAirDrop.getConfigManager().getBossbarTitle("default"));
    }
}