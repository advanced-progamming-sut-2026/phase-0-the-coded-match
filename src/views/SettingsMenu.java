package views;

import controllers.SettingsMenuController;
import enums.Commands;

import java.util.Scanner;

public class SettingsMenu {
    public static void check(String input) {
        if (input.matches(Commands.CHANGE_DIFFICULTY.getPattern())) {
            SettingsMenuController.changeDifficulty(input);
        } else {
            System.out.println("invalid command");
        }
    }
}
