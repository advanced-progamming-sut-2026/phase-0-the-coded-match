package controllers;

import enums.Commands;
import models.*;
import models.plants.Plant;
import models.zombies.Zombie;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameManagerController {
    private static Level currentLevel;

    public static Level getCurrentLevel() {
        return currentLevel;
    }

    public static void setCurrentLevel(Level level) {
        currentLevel = level;
    }

    public static String[] advanceTime(String input, String[] message) {
        message[0] = "";
        Pattern pattern = Pattern.compile(Commands.ADVANCE_TIME.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            message[0] = "invalid command";
            return message;
        }
        int count = Integer.parseInt(matcher.group("count"));
        for (int i = 0; i < count; i++) {
            currentLevel.setCurrentTick(currentLevel.getCurrentTick() + 1);
            updateObjects(message);
        }
        return message;
    }

    public static String[] updateObjects(String[] message) {
        for (Plant plant : currentLevel.getActivePlants()) { //TODO: or a loop on all tiles and their plants?
            plant.update();
            if (plant.getData().getCategory().getName().equalsIgnoreCase("sun producer") && plant.isProducedSun()) {
                message[0] = "plant " + plant.getData().getDisplayName() + " produced a sun at (" + plant.getX() + ", "
                        + plant.getY() + ")";
            }
        }
        for (Zombie zombie : currentLevel.getActiveZombies()) {
            zombie.update();
        }
        currentLevel.getZombieWave().update();
        currentLevel.getSkySunProducer().update();
        if (currentLevel.getSkySunProducer().isProducedASun()) {
            message[0] += "New " + currentLevel.getSkySunProducer().getSun().getType().getName() +
                    " sun is dropping at position (" + currentLevel.getSkySunProducer().getSun().getX() + ", "
                    + currentLevel.getSkySunProducer().getSun().getY() + ")";
            currentLevel.getSkySunProducer().setProducedASun(false);
        }
        for (Sun sun : currentLevel.getActiveSuns()) {
            sun.update();
            if (sun.hasFallen() && !sun.isFalling()) {
                message[0] += "Sun reached the ground at position (" + sun.getX() + ", " + sun.getY() + ")";
            }
        }
        return message;
    }

    public static void collectSun(String input) {
        Pattern pattern = Pattern.compile(Commands.COLLECT_SUN.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return;
        }
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));
        Sun sun = findSun(x, y);
        if (sun != null) {
            sun.collect();
        }
    }

    public static Sun findSun(int x, int y) {
        for (Sun sun : currentLevel.getActiveSuns()) {
            if (sun.getX() == x && sun.getY() == y) {
                return sun;
            }
        }
        return null;
    }

    public static int showSunsAmount() {
        return currentLevel.getCollectedSunsAmount();
    }

    public static void cheatAddSuns(String input) {
        Pattern pattern = Pattern.compile(Commands.CHEAT_ADD_SUNS.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return;
        }
        int count = Integer.parseInt(matcher.group("count"));
        currentLevel.setCollectedSunsAmount(currentLevel.getCollectedSunsAmount() + count);
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

    public static StringBuilder showZombiesInfo() {}

    public static void cheatSpawnZombies(String input) {}

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
