package models.seasons;
import java.util.List;
import enums.SeasonType;
import models.*;
import models.GameMapRelated.Tile;
import models.plants.Plant;
import models.zombies.Zombie;
import models.zombies.ZombieData;
import models.zombies.ZombieRepository;

public abstract class Season {

    protected SeasonData data;
    protected String name;
    private boolean isUnlocked;
    protected SeasonType type;
    protected List<Level> levels;
    protected Tile[][] field;
    private List<ZombieData> allowedZombies;

    public Season(SeasonData data) {
        this.data = data;
    }

    public void initializeGrid() {

    };

    public abstract void LevelStarted(Level level);
    public abstract void Update(Level level);
    public abstract void WaveStarted(Level level, int waveNumber);
    public abstract void PlantPlaced(Level level, Plant plant, int x, int y);

    public Tile getTile(int x, int y) {
        return null;
    }

    public SeasonData getData() {
        return data;
    }

    public List<Level> getLevelsInSeason(){return levels;}

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
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public List<ZombieData> getAllowedZombies() {
        for (ZombieData zombie : ZombieRepository.getInstance().getAllZombies()) {
            if (zombie.getSeasons().contains(this.type)) {
                allowedZombies.add(zombie);
            }
        }
        return allowedZombies;
    }
}

