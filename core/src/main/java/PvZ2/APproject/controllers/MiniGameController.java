package controllers;

import enums.Commands;
import models.Level;
import models.App;
import models.MiniGameRelated.*;
import views.MiniGameView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiniGameController {
    private static MiniGame miniGame;
    private static boolean resultRecorded;
    private Level currentLevel;

    public static String enterMinigame(String input) {
        Pattern pattern = Pattern.compile(Commands.ENTER_MINIGAME.getPattern());
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            return "invalid command";
        }

        String name = matcher.group("name");
        name = name.toLowerCase();
        int stage = Integer.parseInt(matcher.group("level"));
        if (stage <= 0 || stage > 3) {
            return "invalid level";
        }

        switch (name.replace("-", "").replace(" ", "")) {
            case "izombie" -> miniGame = new IZombie(stage);
            case "vasebreaker" -> miniGame = new VaseBreaker(stage);
            case "wallnutbowling" -> miniGame = new WallNutBowling(stage);
            case "beghouled" -> miniGame = new Beghouled(stage);
            case "zombotany" -> miniGame = new Zombotany(stage);
            default -> { return "minigame does not exist"; }
        }
        GameManagerController.getInstance().setCurrentLevel(miniGame);
        resultRecorded = false;
        return "entered minigame " + name + " stage " + stage;
    }

    public void loadMiniGame(){ currentLevel = miniGame; }

    public void StartGame(){
        loadMiniGame();
        if (currentLevel != null) GameManagerController.getInstance().setCurrentLevel(currentLevel);
    }

    public static void verifyWinLossConditions() {
        if (miniGame instanceof IZombie) {
            boolean allBrainsEaten = ((IZombie) miniGame).allBrainsEaten();
            boolean outOfSunAndZombies = ((IZombie) miniGame).getSunAmount() < 50 && miniGame.getActiveZombies().isEmpty();

            if (allBrainsEaten) {
                EndGame(true);
            } else if (outOfSunAndZombies) {
                EndGame(false);
            }
        }
        if (miniGame instanceof VaseBreaker && ((VaseBreaker) miniGame).winConditionsChecked()) EndGame(true);
        if (miniGame instanceof WallNutBowling game && game.isGameOver) EndGame(game.hasWon());
        if (miniGame instanceof Zombotany game && game.isGameOver) EndGame(game.hasWon());
        if (miniGame instanceof Beghouled game) {
            game.checkRules();
            if (game.isGameOver) EndGame(game.hasWon());
        }
    }

    public static void EndGame(Boolean won){
        if (resultRecorded) return;
        resultRecorded = true;
        if (won) {
            if (App.getCurrentUser() != null) {
                App.getCurrentUser().addMinigamesWon();
                controllers.menus.SignupMenuController.saveToJson();
            }
            MiniGameView.miniGameWon();
        } else {
            MiniGameView.miniGameLost();
        }
    }

    public static MiniGame getMiniGame() {
        return miniGame;
    }
}
