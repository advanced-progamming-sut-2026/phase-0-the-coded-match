package models;

import enums.LevelType;
import models.GameMapRelated.GameMap;

import java.util.Collections;
import java.util.List;

public class LevelData {
    private String id;
    private String name;
    private int levelNumber;
    private LevelType levelType;
    private boolean isUnlocked;
    private boolean isDay = true;
    private int plantSelectionLimit;
    private int waveCount;
    private int baseWaveCost;
    private List<String> allowedZombies;
    private List<String> availablePlants;
    private List<String> conveyorPlants;
    private List<String> lockedPlants;
    private List<String> protectedPlants;
    private List<WavePatternData> wavePatterns;
    private List<SpecialRuleData> specialRules;
    private GameMap map;

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
        return allowedZombies == null ? Collections.emptyList() : allowedZombies;
    }

    public List<String> getAvailablePlants() {
        return availablePlants == null ? Collections.emptyList() : availablePlants;
    }

    public GameMap getMap() {
        return map;
    }

    public List<String> getConveyorPlants() {
        return conveyorPlants == null ? Collections.emptyList() : conveyorPlants;
    }

    public List<String> getLockedPlants() {
        return lockedPlants == null ? Collections.emptyList() : lockedPlants;
    }

    public List<SpecialRuleData> getSpecialRules() {
        return specialRules == null ? Collections.emptyList() : specialRules;
    }

    public List<WavePatternData> getWavePatterns() {
        return wavePatterns == null ? Collections.emptyList() : wavePatterns;
    }

    public List<String> getProtectedPlants() {
        return protectedPlants == null ? Collections.emptyList() : protectedPlants;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setLevelNumber(int stageNumber) {
        levelNumber = stageNumber;
    }

    public void setLevelType(LevelType levelType) {
        this.levelType = levelType;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public void setMap(GameMap gameMap) {
        map = gameMap;
    }

    public boolean isDay() {
        return isDay;
    }
}
