package PvZ2.APproject.models.seasons;
import java.util.ArrayList;
import java.util.List;
import PvZ2.APproject.enums.SeasonType;
import PvZ2.APproject.models.*;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.ZombieData;
import PvZ2.APproject.models.zombies.ZombieRepository;

public abstract class Season {

    protected SeasonData data;
    protected String name;
    private boolean isUnlocked;
    protected SeasonType type;
    protected List<Level> levels;
    protected Tile[][] field;
    private List<ZombieData> allowedZombies;

    @SuppressWarnings("this-escape")
    public Season(SeasonData data) {
        this.data = data;
        this.name = data.getDisplayName();
        this.type = data.getSeasonType();
        this.isUnlocked = data.isUnlocked();
        this.levels = new ArrayList<>();
        this.allowedZombies = new ArrayList<>();
        if (data.getLevels() != null) {
            for (LevelData lData : data.getLevels()) {
                if (lData.getLevelNumber() == 1) lData.setUnlocked(true);
                Level lvl = new Level(lData);
                lvl.setCurrentSeason(this);
                this.levels.add(lvl);
            }
        }
    }

    public void initializeGrid() {

    };

    public abstract void LevelStarted(Level level);
    public abstract void Update(Level level);
    public abstract void WaveStarted(Level level, int waveNumber);
    public abstract void PlantPlaced(Level level, Plant plant, int x, int y);

//    public Tile getTile(int x, int y) {
//        return null;
//    }

    public SeasonData getData() {
        return data;
    }

    public List<Level> getLevelsInSeason(){return levels;}

    public List<LevelData> getLevels() {
        return data.getLevels();
    }

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
        allowedZombies.clear();
        for (ZombieData zombie : ZombieRepository.getInstance().getAllZombies()) {
            if (zombie.getSeasons().contains(this.type)) {
                allowedZombies.add(zombie);
            }
        }
        if (allowedZombies.isEmpty()) allowedZombies.addAll(ZombieRepository.getInstance().getAllZombies());
        return allowedZombies;
    }
}

