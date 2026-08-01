package controllers.menus;

import enums.Commands;
import models.App;
import models.Level;
import models.LevelData;
import models.WavePatternData;
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
        int newDifficulty = Integer.parseInt(matcher.group("difficultyLevel"));

        if (newDifficulty == App.getCurrentUser().getDifficultyLevel()) {
            message[0] = "difficulty already set";
            return message;
        } else if (newDifficulty > 5 || newDifficulty < 1) {
            message[0] = "invalid difficulty";
            return message;
        }

        App.getCurrentUser().setDifficultyLevel(newDifficulty);

        message[0] = "Difficulty changed to " + newDifficulty;
        return message;
    }
}
