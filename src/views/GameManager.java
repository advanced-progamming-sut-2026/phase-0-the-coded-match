package views;

import controllers.GameManagerController;
import enums.Commands;

import java.util.Scanner;

public class GameManager {
    public static String[] message = new String[1];

    public static void check(String input) {
        if (input.matches(Commands.ADVANCE_TIME.getPattern())) {
            GameManagerController.advanceTime(input, message);
            System.out.println(message);
        } else if (input.matches(Commands.COLLECT_SUN.getPattern())) {
            GameManagerController.collectSun(input);
        } else if (input.matches(Commands.SUN_AMOUNT.getPattern())) {
            GameManagerController.showSunsAmount();
        } else if (input.matches(Commands.CHEAT_ADD_SUNS.getPattern())) {
            GameManagerController.cheatAddSuns(input);
        }
    }
}
