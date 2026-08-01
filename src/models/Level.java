package models;

import controllers.ZombieWaveManager;
import enums.LevelType;
import models.GameMapRelated.GameMap;
import models.GameMapRelated.SkySunProducer;
import models.GameMapRelated.Tile;
import models.plants.Plant;
import models.seasons.Season;
import models.specialLevels.ConveyorBeltStrategy;
import models.specialLevels.DeadLineStrategy;
import models.specialLevels.LockedPlantsStrategy;
import models.specialLevels.LoveYourPlantsStrategy;
import models.specialLevels.NightOpsStrategy;
import models.specialLevels.PlantWhatYouGetStrategy;
import models.specialLevels.SaveOurSeedsStrategy;
import models.specialLevels.SpecialLevelStrategy;
import models.specialLevels.TimedWarStrategy;
import models.zombies.Barrel;
import models.zombies.Zombie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Level {
    private final LevelData data;
    private int levelNumber;
    private boolean unlocked;
    private LevelType levelType;
    private GameMap gameMap;
    private final List<String> chosenPlants;
    private List<Zombie> activeZombies;
    private final List<Plant> activePlants;
    private List<Sun> activeSuns;
    private int collectedSunsAmount;
    private Season currentSeason;
    private double currentTick;
    private ZombieWaveManager zombieWave;
    private List<Projectile> activeProjectiles;
    private int plantFoodCount;
    private SkySunProducer skySunProducer;
    private List<Barrel> barrels;
    private int removedPlantsCount;
    private SpecialLevelStrategy specialLevel;
    private final int levelDifficulty;
    private int zombiesKilledCount;
    private boolean specialLevelStarted;
    private boolean zombieWavesEnabled = true;
    private final Set<String> boostedPlantNames;

    public Level(LevelData data) {
        this.data = data;
        levelNumber = data.getLevelNumber();
        unlocked = data.isUnlocked();
        levelType = data.getLevelType();
        gameMap = data.getMap() == null ? new GameMap(5, 9) : data.getMap();
        gameMap.initializeGrid();
        chosenPlants = new ArrayList<>();
        boostedPlantNames = new HashSet<>();
        activeZombies = new ArrayList<>();
        activePlants = new ArrayList<>();
        activeSuns = new ArrayList<>();
        activeProjectiles = new ArrayList<>();
        barrels = new ArrayList<>();
        skySunProducer = new SkySunProducer();
        levelDifficulty = App.getCurrentUser() == null ? 3 : App.getCurrentUser().getDifficultyLevel();
        if (App.getCurrentUser() != null && App.getCurrentUser().getPlantFoodBoughtCount() > 0) {
            plantFoodCount = App.getCurrentUser().getPlantFoodBoughtCount();
            App.getCurrentUser().setPlantFoodBoughtCount(0);
        }
        setUpSpecialLevel();
    }

    private void setUpSpecialLevel() {
        if (data.getLevelType() != LevelType.SPECIAL) {
            return;
        }
        for (SpecialRuleData rule : data.getSpecialRules()) {
            String type = rule.getType() == null ? "" : rule.getType();
            switch (type) {
                case "CONVEYOR_SPAWN_INTERVAL_SECONDS" -> specialLevel = new ConveyorBeltStrategy(
                        parseInt(rule.getValue(), 12));
                case "LOCKED_PLANTS_RELOAD" -> specialLevel = new LockedPlantsStrategy();
                case "SAVE_OUR_SEEDS_FAIL_CONDITION" -> specialLevel = new SaveOurSeedsStrategy();
                case "TIMED_WAR_LIMIT_SECONDS" -> specialLevel = new TimedWarStrategy(parseInt(rule.getValue(), 45));
                case "NIGHT_OPS_NO_SKY_SUN" -> specialLevel = new NightOpsStrategy();
                case "DEAD_LINE_LIMIT_COLUMN" -> specialLevel = new DeadLineStrategy(parseInt(rule.getValue(), 3));
                case "MAX_ALLOWED_PLANT_LOSS" -> specialLevel = new LoveYourPlantsStrategy(parseInt(rule.getValue(), 5));
                case "FIXED_INITIAL_SUN_BANK" -> specialLevel = new PlantWhatYouGetStrategy(parseInt(rule.getValue(), 500));
                default -> {
                }
            }
            if (specialLevel != null) {
                break;
            }
        }
    }

    private int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (RuntimeException e) {
            return fallback;
        }
    }

    public Zombie getAdjacentZombie(Zombie zombie) {
        for (Zombie adjacent : activeZombies) {
            if (adjacent != zombie && adjacent.getY() == zombie.getY()
                    && Math.abs(adjacent.getX() - zombie.getX()) <= 0.5) {
                return adjacent;
            }
        }
        return null;
    }

    public boolean isPlantWithinDistance(Zombie zombie, int requiredDistance) {
        for (Plant plant : activePlants) {
            if (plant.getY() == zombie.getY()) {
                double distance = zombie.getX() - plant.getX();
                if (distance >= 0 && distance <= requiredDistance) {
                    return true;
                }
            }
        }
        return false;
    }

    public LevelData getData() {
        return data;
    }

    public void addChosenPlant(String name) {
        chosenPlants.add(name);
    }

    public void addActiveZombie(Zombie zombie) {
        activeZombies.add(zombie);
    }

    public void addActivePlants(Plant plant) {
        activePlants.add(plant);
    }

    public void addActiveSun(Sun sun) {
        activeSuns.add(sun);
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public LevelType getLevelType() {
        return levelType;
    }

    public void setLevelType(LevelType levelType) {
        this.levelType = levelType;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public List<String> getChosenPlants() {
        return chosenPlants;
    }

    public List<Zombie> getActiveZombies() {
        return activeZombies;
    }

    public List<Plant> getActivePlants() {
        return activePlants;
    }

    public void setActiveZombies(List<Zombie> activeZombies) {
        this.activeZombies = activeZombies;
    }

    public List<Sun> getActiveSuns() {
        return activeSuns;
    }

    public void setActiveSuns(List<Sun> activeSuns) {
        this.activeSuns = activeSuns;
    }

    public int getCollectedSunsAmount() {
        return collectedSunsAmount;
    }

    public void setCollectedSunsAmount(int collectedSunsAmount) {
        this.collectedSunsAmount = Math.max(0, collectedSunsAmount);
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(Season currentSeason) {
        this.currentSeason = currentSeason;
    }

    public double getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(double currentTick) {
        this.currentTick = currentTick;
    }

    public ZombieWaveManager getZombieWave() {
        return zombieWave;
    }

    public void setZombieWave(ZombieWaveManager zombieWave) {
        this.zombieWave = zombieWave;
    }

    public List<Projectile> getActiveProjectiles() {
        return activeProjectiles;
    }

    public void setActiveProjectiles(List<Projectile> activeProjectiles) {
        this.activeProjectiles = activeProjectiles;
    }

    public int getPlantFoodCount() {
        return plantFoodCount;
    }

    public void setPlantFoodCount(int plantFoodCount) {
        this.plantFoodCount = Math.max(0, plantFoodCount);
    }

    public SkySunProducer getSkySunProducer() {
        return skySunProducer;
    }

    public void setSkySunProducer(SkySunProducer skySunProducer) {
        this.skySunProducer = skySunProducer;
    }

    public List<Barrel> getBarrels() {
        return barrels;
    }

    public void setBarrels(List<Barrel> barrels) {
        this.barrels = barrels;
    }

    public void addBarrel(Barrel barrel) {
        barrels.add(barrel);
    }

    public Plant getFrontMostPlantInRow(int row, double zombieX) {
        Plant nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Plant plant : activePlants) {
            if (plant.getY() != row || plant.getX() > zombieX + 0.5 || plant.isProtectedFromZombies()) {
                continue;
            }
            double distance = zombieX - plant.getX();
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = plant;
            }
        }
        return nearest;
    }

    public Plant getFrontMostPlantInRow(double row) {
        return getFrontMostPlantInRow((int) row, gameMap.getColumns() + 1);
    }

    public Plant getPlantInFrontOfZombie(Zombie zombie) {
        return getFrontMostPlantInRow(zombie.getY(), zombie.getX());
    }

    public Plant getPlantAt(int x, int y) {
        Tile tile = gameMap.getTile(x, y);
        return tile == null ? null : tile.getPlant();
    }

    public void rebuildPlantTiles() {
        for (int y = 1; y <= gameMap.getRows(); y++) {
            for (int x = 1; x <= gameMap.getColumns(); x++) {
                Tile tile = gameMap.getTile(x, y);
                if (tile != null) {
                    tile.removePlant();
                    tile.setLilyPadPlant(null);
                }
            }
        }
        for (Plant plant : activePlants) {
            Tile tile = gameMap.getTile(plant.getX(), plant.getY());
            if (tile == null) {
                continue;
            }
            String name = plant.getData().getName() == null ? "" : plant.getData().getName();
            if (name.replace("_", " ").replace("-", " ").trim().equalsIgnoreCase("Lily Pad")) {
                tile.setLilyPadPlant(plant);
            } else {
                tile.setPlant(plant);
            }
        }
    }

    public int getRemovedPlantsCount() {
        return removedPlantsCount;
    }

    public void setRemovedPlantsCount(int removedPlantsCount) {
        this.removedPlantsCount = removedPlantsCount;
    }

    public SpecialLevelStrategy getSpecialLevel() {
        return specialLevel;
    }


    public void startLevelMechanics() {
        if (specialLevel != null && !specialLevelStarted) {
            specialLevel.levelStart(this);
            specialLevelStarted = true;
        }
    }

    public boolean isZombieWavesEnabled() {
        return zombieWavesEnabled;
    }

    public void setZombieWavesEnabled(boolean zombieWavesEnabled) {
        this.zombieWavesEnabled = zombieWavesEnabled;
    }

    public int getLevelDifficulty() {
        return levelDifficulty;
    }

    public void boostPlantForLevel(String plantName) {
        if (plantName != null) {
            boostedPlantNames.add(normalizePlantName(plantName));
        }
    }

    public boolean isPlantBoostedForLevel(String plantName) {
        return plantName != null && boostedPlantNames.contains(normalizePlantName(plantName));
    }

    public Set<String> getBoostedPlantNames() {
        return boostedPlantNames;
    }

    private String normalizePlantName(String name) {
        return name.trim().toLowerCase().replace('_', ' ').replace('-', ' ').replaceAll("\\s+", " ");
    }

    public int getZombiesKilledCount() {
        return zombiesKilledCount;
    }

    public void incrementZombiesKilledCount() {
        zombiesKilledCount++;
    }
}
