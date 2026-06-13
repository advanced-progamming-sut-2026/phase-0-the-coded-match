package controllers;

import models.Season;
import models.Sun;
import models.Zombie;
import models.*;

import java.util.List;

public class GameManagerController {

    private Season currentSeason;
    private List<Lawnmower> lawnmowers;
    private List<Plant> plants;
    private List<Zombie> zombies;
    private List<Sun> suns;
    private int currentTick;
    private int waveDifficulty;
    private ZombieWaveManager zombieWave;
    private List<Projectile> activeProjectiles;

    public static void advanceTime(String input) {

    }

    public static void showSunsAmount() {

    }

    public static void cheatAddSuns(String input) {

    }

    public static String[] startWave() {

    }

    public static void cheatReleaseTheNuke() {

    }

    public static void plantPlant(String input) {

    }

    public static void cheatRemoveCooldown() {

    }

    public static void pluckPlant(String input) {

    }

    public static void feedPlant(String input) {

    }

    public static void cheatAddPlantFood() {

    }

    public static StringBuilder showMap() {

    }

    public static StringBuilder showPlantsStatus() {

    }

    public static StringBuilder showTileStatus(String input) {

    }

    public static void ifAZombieWasKilled() {

    }

    public static void endGame() {

    }
    public void saveGame() {

    }
    public void loadGame() {

    }

    public void updateProjectiles() {
    }

    private void handleProjectileCollisions() {}

    private void cleanUpDestroyedProjectiles() {}

    public void spawnProjectile(Projectile projectile) {}
}
