package controllers;

import models.App;
import models.BonusGameRelated.BonusGame;
import models.BonusGameRelated.KillContext;
import models.zombies.Zombie;

public class BonusGameController {
    private static BonusGame currentGame;
    private static long lastKillTime;

    public static String startGame() {
        currentGame = new BonusGame();
        lastKillTime = System.currentTimeMillis();
        if (App.getCurrentUser() != null) {
            App.getCurrentUser().resetMeowPoints();
        }
        return "bonus game started";
    }

    public static String handleZombiesKilled() {
        if (currentGame == null) {
            return "bonus game is not active";
        }
        return handleKill(1, 0, false);
    }

    public static void handleZombieKilled(Zombie zombie, boolean allZombiesDead) {
        if (currentGame == null || zombie == null) {
            return;
        }
        handleKill(1, zombie.getMaxHp(), allZombiesDead);
    }

    public static String handleKill(int killedByOneShot, int damage, boolean allZombiesDead) {
        if (currentGame == null) {
            return "bonus game is not active";
        }
        long now = System.currentTimeMillis();
        KillContext context = new KillContext();
        context.zombiesKilledByOneShot = Math.max(1, killedByOneShot);
        context.timeSinceLastKill = now - lastKillTime;
        context.damageDealt = Math.max(0, damage);
        context.allZombiesDead = allZombiesDead;
        int gained = currentGame.score(context);
        lastKillTime = now;
        if (App.getCurrentUser() != null) {
            App.getCurrentUser().addMeowPoints(gained);
            App.getCurrentUser().setHighestPointAchieved(currentGame.getTotalMioPoints());
        }
        return "gained " + gained + " meowpoints";
    }

    public static String endGame() {
        if (currentGame == null) {
            return "bonus game is not active";
        }
        int score = currentGame.getTotalMioPoints();
        if (App.getCurrentUser() != null) {
            App.getCurrentUser().setHighestPointAchieved(score);
        }
        currentGame = null;
        return "bonus game ended with " + score + " meowpoints";
    }

    public static int getScore() {
        return currentGame == null ? 0 : currentGame.getTotalMioPoints();
    }

    public static boolean isActive() {
        return currentGame != null;
    }

    public static long getDailySeed() {
        return currentGame == null ? java.time.LocalDate.now().toEpochDay() : currentGame.getDailyGameZombies();
    }
}
