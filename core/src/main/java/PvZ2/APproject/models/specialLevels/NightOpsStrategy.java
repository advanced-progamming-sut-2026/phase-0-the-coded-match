package PvZ2.APproject.models.specialLevels;

import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;

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
