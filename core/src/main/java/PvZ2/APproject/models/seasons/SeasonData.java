package models.seasons;

import enums.SeasonType;
import models.LevelData;

import java.util.List;

public class SeasonData {
    private int id;
    private String name;
    private String displayName;
    private String seasonType;
    private boolean isUnlocked;
    private List<String> unlockedPlants;
    private List<LevelData> levels;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public SeasonType getSeasonType() {
        switch (seasonType){
            case "ANCIENT_EGYPT":
                return SeasonType.ANCIENT_EGYPT;
            case "FROSTBITE_CAVES":
                return SeasonType.FROSTBITE_CAVES;
            case "BIG_WAVE_BEACH":
                return SeasonType.BIG_WAVE_BEACH;
            case "DARK_AGES":
                return SeasonType.DARK_AGES;
        }
        return null;
    }

    public List<String> getUnlockedPlants() {
        return unlockedPlants;
    }

    public List<LevelData> getLevels() {
        return levels;
    }
    public boolean isUnlocked(){
        return isUnlocked;
    }
}