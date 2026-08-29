package PvZ2.APproject.models;

import PvZ2.APproject.controllers.ZombieWaveManager;
import PvZ2.APproject.enums.LevelType;
import PvZ2.APproject.enums.SpecialLevelType;
import PvZ2.APproject.models.GameMapRelated.GameMap;
import PvZ2.APproject.models.GameMapRelated.SkySunProducer;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.seasons.Season;
import PvZ2.APproject.models.specialLevels.*;
import PvZ2.APproject.models.zombies.Barrel;
import PvZ2.APproject.models.zombies.Zombie;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Level {

    private LevelData data;
    private int levelNumber;
    private boolean isUnlocked;
    private LevelType levelType;
    private GameMap gameMap; // changed to GameMap from GameMapData double-check with the JSON
    private List<String> chosenPlants;
    private List<Zombie> activeZombies;
    private List<Plant> activePlants;
    private List<Sun> activeSuns; //suns that are on the ground
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
    private int levelDifficulty;

    @SuppressWarnings("this-escape")
    public Level(LevelData data) {
        this.data = data;
        this.levelNumber = data.getLevelNumber();
        this.isUnlocked = data.isUnlocked();
        this.levelType = data.getLevelType();
        this.gameMap = data.getMap();
        if (this.gameMap != null) {
            this.gameMap.initializeGrid();
            this.gameMap.initializeLawnMowers(gameMap.getRows());
        } else {
            this.gameMap = new GameMap(5, 9);
        }
        this.chosenPlants = new ArrayList<>();
        this.activeZombies = new ArrayList<>();
        this.activePlants = new ArrayList<>();
        this.activeSuns = new ArrayList<>();
        this.collectedSunsAmount = 0;
        this.currentSeason = data.getType();
        this.currentTick = 0;
        this.zombieWave = new ZombieWaveManager(this);
        this.activeProjectiles = new ArrayList<>();
        User user = App.getCurrentUser();
        this.plantFoodCount = user == null ? 0 : Math.min(3, user.getPlantFoodBoughtCount());
        if (user != null) user.setPlantFoodBoughtCount(0);
        this.skySunProducer = new SkySunProducer();
        this.barrels = new ArrayList<>();
        this.removedPlantsCount = 0;
        this.levelDifficulty = user == null ? 3 : user.getDifficultyLevel();
        setUpSpecialLevel();
    }

    public void setUpSpecialLevel(){
        Random random = new Random();
        if(this.data.getLevelType() == LevelType.SPECIAL) {
            switch (data.getSpecialLevelType()) {
                case SpecialLevelType.NIGHT_OPS:
                    this.specialLevel = new NightOpsStrategy();
                    break;
                case SpecialLevelType.DEAD_LINE:
                    int deadLine = random.nextInt(6);
                    this.specialLevel = new DeadLineStrategy(deadLine); // e.g., column 4 is the deadline
                    break;
                case SpecialLevelType.LOVE_YOUR_PLANTS:
                    int maxLoss = random.nextInt(6);
                    this.specialLevel = new LoveYourPlantsStrategy(maxLoss); // e.g., max 5 plants lost
                    break;
                case SpecialLevelType.SAVE_OUR_SEEDS:
                    this.specialLevel = new SaveOurSeedsStrategy();
                    break;
                case SpecialLevelType.LOCKED_PLANTS_LEVEL:
                    this.specialLevel = new LockedPlantsLevel();
                    break;
                default:
                    this.specialLevel = null;
                    break;

            }

            if (this.specialLevel!= null) {
                this.specialLevel.levelStart(this);
            }
        }

    }

    public Zombie getAdjacentZombie(Zombie zombie) {
        for (Zombie adjacentZombie : activeZombies) {
            if (zombie.getX() == adjacentZombie.getX() && zombie.getY() == adjacentZombie.getY()) {
                return adjacentZombie;
            }
        }
        return null;
    }

    public Boolean isPlantWithinDistance(Zombie zombie, int requiredDistance) {
        for (Plant plant : activePlants) {
            if (plant.getY() == zombie.getY()) {
                double distance = zombie.getX() - plant.getX();
                if (distance <= requiredDistance) {
                    return true;
                }
            }
        }
        return false;
    }

    public LevelData getData() {return data;}

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
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
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

    public List<Plant> getActivePlants(){return activePlants;}

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
        this.collectedSunsAmount = collectedSunsAmount;
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
        this.plantFoodCount = plantFoodCount;
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

    public Plant getFrontMostPlantInRow(double row){
        Plant front= null;
        int minCol = Integer.MAX_VALUE;
        for(Plant p : activePlants){
            if(p.getY() == row && p.getX() < minCol){
                minCol = p.getX();
                front = p;
            }
        }
        return front;
    }

    public Plant getPlantInFrontOfZombie(Zombie zombie) {
        for (Plant p : activePlants) {
            if (p.getX() <= zombie.getX() + 0.5 && p.getX() >= zombie.getX() && p.getY() == zombie.getY()) {
                return p;
            }
        }
        return null;
    }

    public Plant getPlantAt(int x, int y){
        Tile tile = this.getGameMap().getTile(x, y);
        if (tile == null) return null;
        Plant plant = tile.getPlant();
        if(plant != null){
            return plant;
        }
        return null;
    }

    public int getRemovedPlantsCount() {
        return removedPlantsCount;
    }

    public void setRemovedPlantsCount(int removedPlantsCount) {
        this.removedPlantsCount = removedPlantsCount;
    }

    public SpecialLevelStrategy getSpecialLevel(){
        return specialLevel;
    }

    public int getLevelDifficulty() {
        return levelDifficulty;
    }

    public boolean isDay(){
        return !this.getData().getId().toLowerCase().contains("dark_level") && !(specialLevel instanceof NightOpsStrategy);
    }
}
