package PvZ2.APproject.models.specialLevels;

import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;

import java.util.List;

public interface SpecialLevelStrategy {
    void levelStart(Level level);
    void update(Level level);
    void plantLost(Level level, Plant plant);
    List<Plant> getProtectedPlantsList();
}
