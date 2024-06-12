package org.freeze.loseairdrop.airdrop.strategy;

import org.freeze.loseairdrop.managers.bossbar.BossDisplayManager;

public class WaitingTimeStrategy implements AirDropStateStrategy {

    @Override
    public void execute(BossDisplayManager bossDisplayManager) {
        bossDisplayManager.startUpdateTask();
    }
}
