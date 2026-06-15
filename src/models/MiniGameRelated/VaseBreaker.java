package models.MiniGameRelated;

import enums.VaseType;
import models.DroppedSeedPacket;

import java.util.List;

public class VaseBreaker extends MiniGame {
    private List<VaseType> vases;
    private List<DroppedSeedPacket> active;
    public VaseBreaker(int playerSunAmount, boolean isGameOver) {
        super(playerSunAmount, isGameOver);
    }

    @Override
    public void initializeStage() {

    }

    @Override
    public void processInteraction() {

    }

    @Override
    public void checkRules() {

    }

    void DroppedSeedPackDisappear(){

    }
}
