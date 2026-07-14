package views.menus;

import controllers.menus.LoginMenuController;
import enums.Commands;

public class LoginMenu {
    public static void check(String input) {
        if (input.matches(Commands.LOGIN.getPattern())) {
            LoginMenuController.login(input);
        }
    }
}
