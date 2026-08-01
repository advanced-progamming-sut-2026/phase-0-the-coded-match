package models.specialLevels;

import models.Level;
import models.plants.Plant;

public class LockedPlantsStrategy implements SpecialLevelStrategy {
    @Override
    public void levelStart(Level level) {
        level.getChosenPlants().removeIf(plant -> level.getData().getLockedPlants().stream()
                .anyMatch(locked -> locked.equalsIgnoreCase(plant)));
    }

    @Override
    public void update(Level level) {
    }

    @Override
    public void plantLost(Level level, Plant plant) {
    }
}
