package controllers;

import enums.Commands;
import models.App;
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

    }

    public static void increaseZombiesHp() {
        for (Zombie zombie : App.getAllZombies()) {
            zombie.setCurrentHp(zombie.getCurrentHp());
        }
    }
}
