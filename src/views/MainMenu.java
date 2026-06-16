package views;

import controllers.MainMenuController;
import enums.Commands;

public class MainMenu {
    public static void check(String input) {
        if (input.matches(Commands.LOGOUT.getPattern())) {
            MainMenuController.logout();
        } else {
            System.out.println("invalid command");
        }
    }
}