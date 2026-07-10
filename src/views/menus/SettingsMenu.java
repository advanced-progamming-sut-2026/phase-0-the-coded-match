package views.menus;

import controllers.menus.SettingsMenuController;
import enums.Commands;

public class SettingsMenu {
    public static void check(String input) {
        if (input.matches(Commands.CHANGE_DIFFICULTY.getPattern())) {
            SettingsMenuController.changeDifficulty(input);
        } else {
            System.out.println("invalid command");
        }
    }
}
