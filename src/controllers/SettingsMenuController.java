package controllers;

import enums.Commands;
import enums.SunType;
import models.App;
import models.Level;
import models.LevelData;
import models.zombies.Zombie;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsMenuController{

    public static void changeDifficulty(String input) {
        Pattern pattern = Pattern.compile(Commands.CHANGE_DIFFICULTY.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return;
        }
        int newDifficulty = Integer.parseInt(matcher.group("difficulty_level"));
        App.getCurrentUser().setDifficultyLevel(newDifficulty);
        increaseZombiesHp(newDifficulty);
        decreaseZombieWaveCost(newDifficulty);
        increaseZombieDamage(newDifficulty);
        decreaseSkySunProduction(newDifficulty);
        increaseGameTick(newDifficulty);
    }

    public static void increaseZombiesHp(int dl) {
        for (Zombie zombie : App.getAllZombies()) {
            zombie.setCurrentHp(zombie.getCurrentHp() * (3 / dl));
        }
    }

    public static void decreaseZombieWaveCost(int dl) { //TODO: for all levels?
        Level level;
        for (int i = 0; i < 4; i++) {
            for (LevelData levelData : App.getAllSeasons().get(i).getLevels()) {
                level = new Level(levelData);
                level.getZombieWave().getWavePattern().setWaveDifficulty
                        (level.getZombieWave().getWavePattern().getWaveDifficulty() * (dl / 3)); //TODO: check int or double
            }
        }
    }

    public static void increaseZombieDamage(int dl) {
        for (Zombie zombie : App.getAllZombies()) {
            zombie.setCurrentDamage(zombie.getCurrentDamage() * (3 / dl));
        }
    }

    public static void decreaseSkySunProduction(int dl) {
        //TODO: complete when SkySunProducer is completed
    }

    public static void increaseGameTick(int dl) {
        //TODO: complete when time mechanism is completed
    }
}
