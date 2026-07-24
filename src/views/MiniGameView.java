package views;

import controllers.GameManagerController;
import controllers.MiniGameController;
import enums.Commands;
import models.App;
import models.MiniGameRelated.IZombie;
import models.MiniGameRelated.MiniGame;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiniGameView {
    public static String[] message = new String[1];

    public static void check(String input) {
        Matcher matcher;
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
        } else if ((matcher = getMatcher(input, Commands.VASEBREAKER_BREAK_VASE.getPattern())) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            instance.breakVaseCommand(x, y);
        }else if ((matcher = getMatcher(input, Commands.VASEBREAKER_PICKUP_SEED.getPattern())) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            instance.pickUPPacket(x, y);
        }
    }

    public static void miniGameWon() {
        System.out.println("You won! minigame completed successfully");
    }

    public static void miniGameLost() {
        System.out.println("Game over! couldn't complete minigame");
    }

    private static Matcher getMatcher(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches() ? matcher : null;
    }
}
