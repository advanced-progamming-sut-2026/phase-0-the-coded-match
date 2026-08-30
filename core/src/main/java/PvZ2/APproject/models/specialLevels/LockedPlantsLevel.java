package PvZ2.APproject.models.specialLevels;

import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LockedPlantsLevel implements SpecialLevelStrategy{
    private final List<String> lockedPlantNames = new ArrayList<>();
    @Override
    public void levelStart(Level level) {
        Random random = new Random();
        List<String> allPlants = level.getData().getLockedPlants();
        if (allPlants == null || allPlants.isEmpty()) allPlants = level.getData().getAvailablePlants();
        if (!allPlants.isEmpty()) {
            int lockCount = Math.min(allPlants.size(), 2 + random.nextInt(2));
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

    @Override
    public List<Plant> getProtectedPlantsList() {
        return List.of();
    }

    public boolean isPlantLocked(String plantName) {
        return lockedPlantNames.contains(plantName);
    }

    public List<String> getLockedPlantNames() {
        return lockedPlantNames;
    }
}
