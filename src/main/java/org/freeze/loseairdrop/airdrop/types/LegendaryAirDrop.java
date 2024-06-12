package org.freeze.loseairdrop.airdrop.types;

import org.freeze.loseairdrop.LoseAirDrop;
import org.freeze.loseairdrop.airdrop.AirDrop;

public class LegendaryAirDrop extends AirDrop {
    public LegendaryAirDrop() {
        super(LoseAirDrop.getConfigManager().getBossbarTitle("legendary"));
    }
}