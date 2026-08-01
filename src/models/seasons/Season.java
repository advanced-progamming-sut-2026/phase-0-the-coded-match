package models.seasons;

import enums.SeasonType;
import models.Level;
import models.LevelData;
import models.SpecialFeatureData;
import models.GameMapRelated.Tile;
import models.plants.Plant;

import java.util.ArrayList;
import java.util.List;

public abstract class Season {
    protected final SeasonData data;
    protected final String name;
    private boolean unlocked;
    protected final SeasonType type;
    protected final List<Level> levels;
    protected Tile[][] field;

    public Season(SeasonData data) {
        this.data = data;
        this.name = data.getDisplayName() == null ? data.getName() : data.getDisplayName();
        this.type = SeasonType.valueOf(data.getSeasonType());
        this.levels = new ArrayList<>();
    }

    public void initializeGrid() {
    }

    public abstract void LevelStarted(Level level);

    public abstract void Update(Level level);

    public abstract void WaveStarted(Level level, int waveNumber);

    public abstract void PlantPlaced(Level level, Plant plant, int x, int y);

    public Tile getTile(int x, int y) {
        if (field == null || y < 1 || y > field.length || x < 1 || x > field[0].length) {
            return null;
        }
        return field[y - 1][x - 1];
    }

    public SeasonData getData() {
        return data;
    }

    public List<Level> getLevelsInSeason() {
        return levels;
    }

    public List<LevelData> getLevels() {
        return data.getLevels();
    }

    public List<SpecialFeatureData> getSpecialFeatures() {
        return data.getSpecialFeatures();
    }

    public abstract void applySpecialRules();

    public SeasonType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }
}
