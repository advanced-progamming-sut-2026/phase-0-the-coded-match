package views.menus;

import controllers.menus.SettingsMenuController;
import enums.Commands;

public class SettingsMenu {
    public static String[] message = new String[1];

    public static void check(String input) {
        if (input.matches(Commands.CHANGE_DIFFICULTY.getPattern())) {
            SettingsMenuController.changeDifficulty(input, message);
        } else {
            message[0] = "invalid command";
        }

        System.out.println(message[0]);
    }
}
