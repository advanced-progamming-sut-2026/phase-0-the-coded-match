package PvZ2.APproject.views;

import PvZ2.APproject.controllers.BonusGameController;

public class BonusGameView {
    public static void check(String input) {
        if (input.matches("^\\s*start\\s+game\\s*$")) System.out.println(BonusGameController.startGame());
        else if (input.matches("^\\s*show\\s+score\\s*$")) System.out.println("score: " + (BonusGameController.getGame() == null ? 0 : BonusGameController.getGame().getTotalMioPoints()));
        else if (input.matches("^\\s*next\\s+zombie\\s*$")) System.out.println(BonusGameController.spawnNextZombie());
        else if (input.matches("^\\s*end\\s+game\\s*$")) System.out.println(BonusGameController.endGame());
        else System.out.println("invalid command");
    }
}
