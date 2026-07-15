package controllers.menus;

import enums.Commands;
import models.App;
import models.Level;
import models.LevelData;
import models.zombies.Zombie;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsMenuController{

    public static String[] changeDifficulty(String input, String[] message) {
        Pattern pattern = Pattern.compile(Commands.CHANGE_DIFFICULTY.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            message[0] = "invalid command";
            return message;
        }
        int newDifficulty = Integer.parseInt(matcher.group("difficulty_level"));
        App.getCurrentUser().setDifficultyLevel(newDifficulty);

        increaseZombiesHp(newDifficulty);
        decreaseZombieWaveCost(newDifficulty);
        increaseZombieDamage(newDifficulty);
        message[0] = "Difficulty changed to " + newDifficulty;
        return message;
    }

    public static void increaseZombiesHp(int dl) {
        for (Zombie zombie : App.getAllZombies()) {
            zombie.setCurrentHp((int) (zombie.getCurrentHp() * (3.0 / dl)));
        }
    }

    public static void decreaseZombieWaveCost(int dl) { //TODO: for all levels?
        Level level;
        for (int i = 0; i < 4; i++) {
            for (LevelData levelData : App.getAllSeasons().get(i).getLevels()) {
                level = new Level(levelData);
                level.getZombieWave().getWavePattern().setWaveDifficulty
                        (level.getZombieWave().getWavePattern().getWaveDifficulty() * (dl / 3.0));
            }
        }
    }

    public static void increaseZombieDamage(int dl) {
        for (Zombie zombie : App.getAllZombies()) {
            zombie.setEatDPS((int) (zombie.getEatDPS() * (3.0 / dl)));
        }
    }
}
