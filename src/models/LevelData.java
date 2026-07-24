package models;

import enums.LevelType;
import models.GameMapRelated.GameMap;
import models.seasons.Season;
import models.specialLevels.SpecialLevelStrategy;

import java.util.List;

public class LevelData {
    private String id;
    private String name;
    private int levelNumber;
    private LevelType levelType;
    private boolean isUnlocked;
    private boolean isDay;
    private int plantSelectionLimit;
    private int waveCount;
    private int baseWaveCost;
    private Season type;
    private List<String> allowedZombies;
    private List<String> availablePlants;
    private List<String> conveyorPlants;
    private List<String> lockedPlants;
    private List<String> protectedPlants;
    private List<WavePatternData> wavePatterns; //i changed it from List to WavePatternData cause each level has multiple waves but starts from one pattern!!
    private List<SpecialLevelStrategy> specialRules;
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

    public List<String> getAllowedZombies() {
        return allowedZombies;
    }

    public List<String> getAvailablePlants() {
        return availablePlants;
    }

    public GameMap getMap() {
        return map;
    }

    public List<String> getConveyorPlants() {
        return conveyorPlants;
    }

    public List<String> getLockedPlants() {
        return lockedPlants;
    }

    public List<SpecialLevelStrategy> getSpecialRules() {
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

    public void setLevelNumber(int stageNumber) { this.levelNumber = stageNumber;}

    public void setLevelType(LevelType levelType) { this.levelType = levelType;
    }

    public void setUnlocked(boolean unlocked) { this.isUnlocked = unlocked;}

    public void setMap(GameMap gameMap) { this.map = gameMap;}

    public boolean isDay() {
        return isDay;
    }
}