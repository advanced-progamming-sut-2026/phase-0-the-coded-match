package models.specialLevels;

import models.Level;
import models.plants.Plant;

public interface SpecialLevelStrategy {
    void levelStart(Level level);
    void update(Level level);
    void plantLost(Level level, Plant plant);
}
