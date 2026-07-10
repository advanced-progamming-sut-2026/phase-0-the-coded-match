package models;

import controllers.ZombieWaveManager;
import enums.LevelType;
import models.GameMapRelated.GameMap;
import models.GameMapRelated.GameMapData;
import models.GameMapRelated.SkySunProducer;
import models.plants.Plant;
import models.seasons.Season;
import models.zombies.Barrel;
import models.zombies.Zombie;

import java.util.ArrayList;
import java.util.List;

public class Level {

    private LevelData data;
    private int levelNumber;
    private boolean isUnlocked;
    private LevelType levelType;
    private GameMap gameMap; // changed to GameMap from GameMapData double-check with he JSON
    private List<Zombie> activeZombies;
    private List<Plant> activePlants;
    private List<Sun> activeSuns; //suns that are on the ground
    private int collectedSunsAmount;
    private Season currentSeason;
    private int currentTick;
    private ZombieWaveManager zombieWave;
    private List<Projectile> activeProjectiles; 
    private int plantFoodCount;
    private SkySunProducer skySunProducer;
    private List<Barrel> barrels;
    private int removedPlantsCount;

    public Level(LevelData data) {
        this.data = data;
        this.levelNumber = data.getLevelNumber();
        this.isUnlocked = data.isUnlocked();
        this.levelType = data.getLevelType();
        this.gameMap = data.getMap();
        this.activeZombies = new ArrayList<>();
        this.activePlants = new ArrayList<>();
        this.activeSuns = new ArrayList<>();
        this.collectedSunsAmount = 0;
        this.currentSeason = data.getType();
        this.currentTick = 0;
        this.zombieWave = null;
        this.activeProjectiles = new ArrayList<>();
        this.plantFoodCount = 0;
        skySunProducer = new SkySunProducer();
        this.barrels = new ArrayList<>();
        this.removedPlantsCount = 0;
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

    public void addActiveZombie(Zombie zombie) {
        activeZombies.add(zombie);
    }

    public void AddActivePlants(Plant plant) {
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

    public List<Barrel> getBarrels() {
        return barrels;
    }

    public void setBarrels(List<Barrel> barrels) {
        this.barrels = barrels;
    }

    public Plant getFrontMostPlantInRow(double row){
        Plant front= null;
        int minCol = Integer.MAX_VALUE;
        for(Plant p : activePlants){
            if(p.getX() == row && p.getY() < minCol){
                minCol = p.getY();
                front = p;
            }
        }
        return front;
    }

    public Plant getPlantInFrontOfZombie(Zombie zombie) {
        Plant target = null;
        for (Plant p : activePlants) {
            if (p.getX() == zombie.getX() && p.getY() == zombie.getY()) {
                return target;
            }
        }
        return null;
    }

    public int getRemovedPlantsCount() {
        return removedPlantsCount;
    }

    public void setRemovedPlantsCount(int removedPlantsCount) {
        this.removedPlantsCount = removedPlantsCount;
    }
}