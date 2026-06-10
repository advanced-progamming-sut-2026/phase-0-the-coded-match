package Model;

import java.util.List;

public class LevelData {
    private String id;
    private String name;
    private int levelNumber;
    private String levelType;
    private int plantSelectionLimit;
    private int waveCount;
    private int baseWaveCost;
    private List<String> allowedZombies;
    private List<String> availablePlants;
    private MapData map;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public String getLevelType() {
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

    public MapData getMap() {
        return map;
    }
}