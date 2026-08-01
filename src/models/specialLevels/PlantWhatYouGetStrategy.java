package models.specialLevels;

import models.Level;
import models.plants.Plant;

public class PlantWhatYouGetStrategy implements SpecialLevelStrategy {
    private final int initialSuns;

    public PlantWhatYouGetStrategy(int initialSuns) {
        this.initialSuns = Math.max(0, initialSuns);
    }

    @Override
    public void levelStart(Level level) {
        level.setCollectedSunsAmount(initialSuns);
        level.setSkySunProducer(null);
        level.setZombieWavesEnabled(false);
        level.getChosenPlants().removeIf(name -> name.toLowerCase().contains("sunflower")
                || name.toLowerCase().contains("sun-shroom"));
    }

    @Override
    public void update(Level level) {
    }

    @Override
    public void plantLost(Level level, Plant plant) {
    }
}
