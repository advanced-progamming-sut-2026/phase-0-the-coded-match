package PvZ2.APproject.views.menus;

import PvZ2.APproject.controllers.menus.MainMenuController;
import PvZ2.APproject.enums.Commands;

public class MainMenu {
    public static void check(String input) {
        if (input.matches(Commands.LOGOUT.getPattern())) {
            MainMenuController.logout();
        } else {
            System.out.println("invalid command");
        }
    }
}
