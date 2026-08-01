package models.specialLevels;

import controllers.GameManagerController;
import controllers.SeasonController;
import models.Level;
import models.SeedPacket;
import models.plants.Plant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LockedPlantsLevel implements SpecialLevelStrategy{
    private final List<String> lockedPlantNames = new ArrayList<>();
    @Override
    public void levelStart(Level level) {
        Random random = new Random();
        List<String> allPlants = SeasonController.getInstance().getCurrentSeason().getData().getUnlockedPlants();
        if (!allPlants.isEmpty()) {
            int lockCount = 2 + random.nextInt(2);
            for (int i = 0; i < lockCount; i++) {
                String nameToLock = allPlants.get(random.nextInt(allPlants.size()));
                if (!lockedPlantNames.contains(nameToLock)) {
                    lockedPlantNames.add(nameToLock);
                }
            }
        }
    }

    @Override
    public void update(Level level) {

    }

    @Override
    public void plantLost(Level level, Plant plant) {

    }

    public boolean isPlantLocked(String plantName) {
        return lockedPlantNames.contains(plantName);
    }

    public List<String> getLockedPlantNames() {
        return lockedPlantNames;
    }
}
