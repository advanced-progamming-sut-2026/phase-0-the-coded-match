package models;

import enums.LevelType;
import enums.SeasonType;

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
    private List<String> allowedZombies;
    private List<String> availablePlants;
    private List<String> conveyorPlants;
    private List<String> lockedPlants;
    private List<String> protectedPlants;
    private List<WavePatternData> wavePatterns;
    private List<SpecialLevelRuleData> specialRules;
    private GameMapData map;

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

    public List<String> getAllowedZombies() {
        return allowedZombies;
    }

    public List<String> getAvailablePlants() {
        return availablePlants;
    }

    public GameMapData getMap() {
        return map;
    }

    public List<String> getConveyorPlants() {
        return conveyorPlants;
    }

    public List<String> getLockedPlants() {
        return lockedPlants;
    }

    public List<SpecialLevelRuleData> getSpecialRules() {
        return specialRules;
    }

    public List<WavePatternData> getWavePatterns() {
        return wavePatterns;
    }

    public List<String> getProtectedPlants() {
        return protectedPlants;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public Season getType() {
        return type;
    }
}