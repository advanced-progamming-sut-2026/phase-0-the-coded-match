package views;

import controllers.GameMenuController;
import enums.Commands;

public class GameMenu {
    public static void check(String input) {
        if (input.matches(Commands.ENTER_SEASON.getPattern())) {
            GameMenuController.enterSeason(input);
        } else if (input.matches(Commands.ENTER_LEVEL.getPattern())) {
            GameMenuController.enterLevel(input);
        } else if (input.matches(Commands.GAME_MENU_MENUS.getPattern())) {
            GameMenuController.enter(input);
        } else {
            System.out.println("invalid command");
        }
    }
}
