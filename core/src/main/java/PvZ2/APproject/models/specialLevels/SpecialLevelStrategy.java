package PvZ2.APproject.models.specialLevels;

import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;

public interface SpecialLevelStrategy {
//    private SpecialLevelType type;
//    private String value;
//    private String description;
//    private boolean enabled;
//
//    public SpecialLevelType getType() {
//        return type;
//    }
//
//    public String getValue() {
//        return value;
//    }
//
//    public String getDescription() {
//        return description;
//    }

    void levelStart(Level level);
    void update(Level level);
    void plantLost(Level level, Plant plant);
}
