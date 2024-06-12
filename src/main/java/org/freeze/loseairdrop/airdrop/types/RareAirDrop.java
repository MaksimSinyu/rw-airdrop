package org.freeze.loseairdrop.airdrop.types;

import org.freeze.loseairdrop.LoseAirDrop;
import org.freeze.loseairdrop.airdrop.AirDrop;

public class RareAirDrop extends AirDrop {
    public RareAirDrop() {
        super(LoseAirDrop.getConfigManager().getBossbarTitle("rare"));
    }
}