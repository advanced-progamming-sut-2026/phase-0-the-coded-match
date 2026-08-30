package PvZ2.APproject.models.specialLevels;

import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;

import java.util.List;

public class NightOpsStrategy implements SpecialLevelStrategy {
    @Override
    public void levelStart(Level level) {
        level.setSkySunProducer(null);
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
}
