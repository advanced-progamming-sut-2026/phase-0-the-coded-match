package views.menus;

import controllers.menus.GameMenuController;
import enums.Commands;

public class GameMenu {
    public static String[] message = new String[1];

    public static void check(String input) {
        if (input.matches(Commands.ENTER_SEASON.getPattern())) {
            GameMenuController.enterSeason(input, message);
        } else if (input.matches(Commands.ENTER_LEVEL.getPattern())) {
            GameMenuController.enterLevel(input, message);
        } else if (input.matches(Commands.GAME_MENU_MENUS.getPattern())) {
            GameMenuController.enter(input, message);
        } else if (input.matches(Commands.CHEAT_ADD_CURRENCY.getPattern())) {
            GameMenuController.cheatAddCoinOrGem(input, message);
        } else {
            message[0] = "invalid command";
        }

        System.out.println(message[0]);
    }
}
