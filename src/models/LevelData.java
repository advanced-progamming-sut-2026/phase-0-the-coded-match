package models;

import enums.LevelType;
import models.GameMapRelated.GameMap;
import models.plants.Plant;
import models.GameMapRelated.GameMapData;
import models.seasons.Season;
import models.zombies.Zombie;

import java.util.List;

public class LevelData {
    private String id;
    private String name;
    private int levelNumber;
    private LevelType levelType;
    private boolean isUnlocked;
    private int plantSelectionLimit;
    private int waveCount;
    private int baseWaveCost;
    private Season type;
    private List<Zombie> allowedZombies;
    private List<Plant> availablePlants;
    private List<Plant> conveyorPlants;
    private List<Plant> lockedPlants;
    private List<Plant> protectedPlants;
    private WavePatternData wavePatterns; //i changed it from List to WavePatternData cause each level has multiple waves but starts from one pattern!!
    private List<SpecialLevelRuleData> specialRules;
    private GameMap map; //DOUBLE CHECK WITH JSON

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public LevelType getLevelType() {
        return levelType;
    }

    public int getPlantSelectionLimit() {
        return plantSelectionLimit;
    }

    public int getWaveCount() {
        return waveCount;
    }

    public int getBaseWaveCost() {
        return baseWaveCost;
    }

    public List<Zombie> getAllowedZombies() {
        return allowedZombies;
    }

    public List<Plant> getAvailablePlants() {
        return availablePlants;
    }

    public GameMap getMap() {
        return map;
    }

    public List<Plant> getConveyorPlants() {
        return conveyorPlants;
    }

    public List<Plant> getLockedPlants() {
        return lockedPlants;
    }

    public List<SpecialLevelRuleData> getSpecialRules() {
        return specialRules;
    }

    public WavePatternData getWavePatterns() {
        return wavePatterns;
    }

    public List<Plant> getProtectedPlants() {
        return protectedPlants;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public Season getType() {
        return type;
    }
}