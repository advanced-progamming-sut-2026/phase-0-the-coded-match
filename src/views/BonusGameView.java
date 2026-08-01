package views;

import controllers.BonusGameController;

public class BonusGameView {
    public static void check(String input) {
        if (input.equalsIgnoreCase("start game")) {
            System.out.println(BonusGameController.startGame());
        } else if (input.equalsIgnoreCase("show score")) {
            System.out.println(BonusGameController.getScore());
        } else if (input.equalsIgnoreCase("end game")) {
            System.out.println(BonusGameController.endGame());
        } else {
            System.out.println("invalid command");
        }
    }
}
