package models;

import controllers.ZombieWaveManager;
import enums.LevelType;
import models.GameMapRelated.GameMapData;
import models.GameMapRelated.SkySunProducer;
import models.seasons.Season;
import models.zombies.Zombie;

import java.util.ArrayList;
import java.util.List;

public class Level {

    private LevelData data;
    private int levelNumber;
    private boolean isUnlocked;
    private LevelType levelType;
    private GameMapData gameMap;
    private List<Zombie> activeZombies;
    private List<Sun> activeSuns; //todo: suns that are on the ground
    private int collectedSunsAmount;
    private Season currentSeason;
    private int currentTick;
    private ZombieWaveManager zombieWave;
    private List<Projectile> activeProjectiles; //todo: here or in GameMap or not needed
    private int plantFoodCount;
    private SkySunProducer skySunProducer;

    public Level(LevelData data) {
        this.data = data;
        this.levelNumber = data.getLevelNumber();
        this.isUnlocked = data.isUnlocked();
        this.levelType = data.getLevelType();
        this.gameMap = data.getMap();
        this.activeZombies = new ArrayList<>();
        this.activeSuns = new ArrayList<>();
        this.collectedSunsAmount = 0;
        this.currentSeason = data.getType();
        this.currentTick = 0;
        this.zombieWave = data.; //TODO: what type of wave exactly? ZombieWaveController or WavePatterData?
        this.activeProjectiles = new ArrayList<>();
        this.plantFoodCount = 0;
    }

    public void addActiveZombie(Zombie zombie) {}

    public void addActiveSun() {}

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

    public GameMapData getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMapData gameMap) {
        this.gameMap = gameMap;
    }

    public List<Zombie> getActiveZombies() {
        return activeZombies;
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
        this.collectedSunsAmount = collectedSunsAmount;
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(Season currentSeason) {
        this.currentSeason = currentSeason;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(int currentTick) {
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
}
