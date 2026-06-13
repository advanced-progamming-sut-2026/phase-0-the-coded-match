package models;

import controllers.ZombieWaveManager;
import enums.LevelType;

import java.util.List;

public class Level {

    private int levelNumber;
    private boolean isUnlocked;
    private LevelType levelType;
    private int difficulty;
    private GameMap gameMap;
    private List<Zombie> activeZombies;
    private List<Sun> activeSuns; //todo: suns that are on the ground
    private int collectedSunsCount;
    private Season currentSeason;
    private int currentTick;
    private ZombieWaveManager zombieWave;
    private List<Projectile> activeProjectiles; //todo: here or in GameMap or not needed

    public void addActiveZombie(Zombie zombie) {}

    public void addActiveSun() {}
}
