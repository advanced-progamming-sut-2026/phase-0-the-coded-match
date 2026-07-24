package views;

import controllers.GameManagerController;
import controllers.MiniGameController;
import enums.Commands;
import models.MiniGameRelated.IZombie;
import models.MiniGameRelated.MiniGame;

public class MiniGameView {
    public static String[] message = new String[1];

    public static void check(String input) {
        MiniGame miniGame = null;
        GameManagerController instance = GameManagerController.getInstance();
        if (input.matches(Commands.ENTER_MINIGAME.getPattern())) {
            miniGame = MiniGameController.getMiniGame();
            System.out.println(MiniGameController.enterMinigame(input));
        } else if (input.matches(Commands.ADVANCE_TIME.getPattern())) {
            instance.advanceTime(input, message);
            if (miniGame instanceof IZombie) {
                ((IZombie) miniGame).Update();
            }
            System.out.println(message[0]);
        } else if (input.matches(Commands.PLACE_ZOMBIE.getPattern())) {
            ((IZombie) miniGame).placeZombie(input);
        }
    }

    public static void miniGameWon() {
        System.out.println("You won! minigame completed successfully");
    }

    public static void miniGameLost() {
        System.out.println("Game over! couldn't complete minigame");
    }
}
