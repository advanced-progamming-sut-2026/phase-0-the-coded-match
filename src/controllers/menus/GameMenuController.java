package controllers.menus;

import controllers.GameManagerController;
import enums.Commands;
import enums.Menu;
import models.App;
import models.Level;
import models.LevelData;
import models.MiniGameRelated.VaseBreaker;
import models.seasons.Season;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameMenuController{

    public static String[] enterSeason(String input, String[] message) {
        Pattern pattern = Pattern.compile(Commands.ENTER_SEASON.getPattern());
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            message[0] = "Invalid command";
            return message;
        }

        String seasonName = matcher.group("season");
        Season season = App.getSeason(seasonName);

        if (season.isUnlocked()) {
            App.getCurrentUser().setLastSeason(season);
            message[0] = "entered " + seasonName + " successfully";
        } else {
            message[0] = seasonName + " is locked";
        }
        return message;
    }

    public static String[] enterLevel(String input, String[] message) {
        Pattern pattern = Pattern.compile(Commands.ENTER_LEVEL.getPattern());
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            message[0] = "Invalid command";
            return message;
        }

        int levelNum = Integer.parseInt(matcher.group("level"));
        LevelData level = App.getLevelByNumber(levelNum, App.getCurrentUser().getLastSeason());

        if (level.isUnlocked()) {
            App.getCurrentUser().setLastLevel(level);
            Level currentLevel = new Level(level);
            GameManagerController.getInstance().setCurrentLevel(currentLevel);
            message[0] = "entered level " + levelNum + " successfully";
        } else {
            message[0] = "level" + levelNum + " is locked";
        }
        return message;
    }

    public static void enterMiniGame(String gameName, int stageNumber){ // I am not sure if this is how we should enter the minigames
        switch (gameName.toLowerCase()){
            case "vasebreaker":
                VaseBreaker minigameLevel = new VaseBreaker(stageNumber);
                GameManagerController.getInstance().setCurrentLevel(minigameLevel);
            case "":
        }


    }

    public static String[] enter(String input, String[] message) {
        Pattern pattern = Pattern.compile(Commands.GAME_MENU_MENUS.getPattern());
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            message[0] = "Invalid command";
            return message;
        }

        String menuSt = matcher.group("menu");
        Menu menu = App.getMenu(menuSt);
        App.setCurrentMenu(menu);
        message[0] = "entered " + menuSt + "successfully";
        return message;
    }

    public static String[] cheatAddCoinOrGem(String input, String[] message) {
        Pattern pattern = Pattern.compile(Commands.CHEAT_ADD_CURRENCY.getPattern());
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            message[0] = "Invalid command";
            return message;
        }

        int amount = Integer.parseInt(matcher.group("amount"));
        String currency = matcher.group("currency");
        if (currency.equals("coin")) {
            App.getCurrentUser().addCoins(amount);
        } else {
            App.getCurrentUser().addGems(amount);
        }
        message[0] = amount + currency + " added successfully";
        return message;
    }


}
