package models.specialLevels;

import models.Level;
import models.plants.Plant;

public class NightOpsStrategy implements SpecialLevelStrategy {
    @Override
    public void levelStart(Level level) {
        level.setSkySunProducer(null);// maybe will add a boolean enabler later
    }

    @Override
    public void update(Level level) {

    }

    @Override
    public void plantLost(Level level, Plant plant) {

    }
}
