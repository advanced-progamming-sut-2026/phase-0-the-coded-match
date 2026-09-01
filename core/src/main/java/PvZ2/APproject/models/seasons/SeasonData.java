package PvZ2.APproject.models.seasons;

import PvZ2.APproject.enums.SeasonType;
import PvZ2.APproject.models.LevelData;

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
        if (seasonType == null) return null;
        try {
            return SeasonType.valueOf(seasonType.trim().toUpperCase().replace(' ', '_').replace('-', '_'));
        } catch (IllegalArgumentException e) {
            return null;
        }
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
