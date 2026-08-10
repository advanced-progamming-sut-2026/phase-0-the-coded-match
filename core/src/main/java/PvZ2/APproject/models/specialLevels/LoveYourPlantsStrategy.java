package models.specialLevels;

import controllers.GameManagerController;
import models.Level;
import models.plants.Plant;

public class LoveYourPlantsStrategy implements SpecialLevelStrategy {
    private final int maxAllowedLosses;
    private int lostPlantsCount = 0;

    public LoveYourPlantsStrategy(int maxAllowedLosses) {
        this.maxAllowedLosses = maxAllowedLosses;
    }

    @Override
    public void levelStart(Level level) {

    }

    @Override
    public void update(Level level) {

    }

    @Override
    public void plantLost(Level level, Plant plant) {
        lostPlantsCount++;

        if (lostPlantsCount >= maxAllowedLosses) {
            GameManagerController.getInstance().gameOver();
        }
    }
}
