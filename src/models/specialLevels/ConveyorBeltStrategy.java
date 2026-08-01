package models.specialLevels;

import models.Level;
import models.plants.Plant;

import java.util.List;
import java.util.Random;

public class ConveyorBeltStrategy implements SpecialLevelStrategy {
    private final int intervalTicks;
    private final Random random = new Random();
    private double lastSpawnTick;

    public ConveyorBeltStrategy(int intervalSeconds) {
        intervalTicks = Math.max(1, intervalSeconds * 10);
    }

    @Override
    public void levelStart(Level level) {
        lastSpawnTick = level.getCurrentTick() - intervalTicks;
        addPlant(level);
    }

    @Override
    public void update(Level level) {
        if (level.getCurrentTick() - lastSpawnTick >= intervalTicks) {
            addPlant(level);
        }
    }

    private void addPlant(Level level) {
        List<String> plants = level.getData().getConveyorPlants();
        if (plants.isEmpty()) {
            return;
        }
        level.getChosenPlants().add(plants.get(random.nextInt(plants.size())));
        lastSpawnTick = level.getCurrentTick();
    }

    @Override
    public void plantLost(Level level, Plant plant) {
    }
}
