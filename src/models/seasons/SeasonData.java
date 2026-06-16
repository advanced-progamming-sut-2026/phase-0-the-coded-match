package models.seasons;

import models.LevelData;
import models.SpecialFeatureData;

import java.util.List;

public class SeasonData {
    private String id;
    private String name;
    private String displayName;
    private String seasonType;
    private List<String> allowedZombies;
    private List<String> unlockedPlants;
    private List<SpecialFeatureData> specialFeatures;
    private List<LevelData> levels;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSeasonType() {
        return seasonType;
    }

    public List<String> getAllowedZombies() {
        return allowedZombies;
    }

    public List<String> getUnlockedPlants() {
        return unlockedPlants;
    }

    public List<SpecialFeatureData> getSpecialFeatures() {
        return specialFeatures;
    }

    public List<LevelData> getLevels() {
        return levels;
    }
}