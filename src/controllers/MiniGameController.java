package controllers;

import enums.Commands;
import models.Level;
import models.MiniGameRelated.IZombie;
import models.MiniGameRelated.MiniGame;
import models.MiniGameRelated.VaseBreaker;
import views.MiniGameView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiniGameController {
    private static MiniGame miniGame;
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

        switch (name) {
            case "izombie" :
                miniGame = new IZombie(stage);
            case "vasebreaker":
                miniGame = new VaseBreaker(stage);

        }

        return "entered minigame" + name;
    }

    public void loadMiniGame(){}

    public void StartGame(){}

    public static void verifyWinLossConditions() {
        if (miniGame instanceof IZombie) {
            boolean allBrainsEaten = false;
            boolean outOfSunAndZombies = (((IZombie) miniGame).getSunAmount() < 50 &&
                    miniGame.getActiveZombies().isEmpty());

            if (allBrainsEaten) {
                EndGame(true);
            } else if (outOfSunAndZombies) {
                EndGame(false);
            }
        }
        if(miniGame instanceof VaseBreaker){
            if(((VaseBreaker) miniGame).winConditionsChecked()){
                EndGame(true);
            }else{
                EndGame(false);
            }
        }
    }

    public static void EndGame(Boolean won){
        if (won) {
            MiniGameView.miniGameWon();
        } else {
            MiniGameView.miniGameLost();
        }
    }

    public static MiniGame getMiniGame() {
        return miniGame;
    }
}
