package PvZ2.APproject.models.specialLevels;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;

import java.util.List;

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

        if (lostPlantsCount > maxAllowedLosses) {
            GameManagerController.getInstance().gameOver();
        }
    }

    @Override
    public List<Plant> getProtectedPlantsList() {
        return List.of();
    }

    public int getMaxAllowedLosses() {
        return maxAllowedLosses;
    }

    public int getLostPlantsCount() {
        return lostPlantsCount;
    }
}
