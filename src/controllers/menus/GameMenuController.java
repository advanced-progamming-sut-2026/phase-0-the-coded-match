package controllers.menus;

import controllers.GameManagerController;
import controllers.SeasonController;
import enums.Commands;
import enums.Menu;
import models.App;
import models.Level;
import models.LevelData;
import models.MiniGameRelated.VaseBreaker;
import models.seasons.Season;
import views.AppView;

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

        if (season == null) {
            message[0] = "season does not exist";
        } else if (season.isUnlocked()) {
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

        if (level == null) {
            message[0] = "level does not exist";
        } else if (level.isUnlocked()) {
            App.getCurrentUser().setLastLevel(level);
            Level currentLevel = new Level(level);
            currentLevel.setCurrentSeason(App.getCurrentUser().getLastSeason());
            GameManagerController.getInstance().setCurrentLevel(currentLevel);
            App.setCurrentMenu(Menu.CHOOSEPLANTS_MENU);
            message[0] = "entered level " + levelNum + " successfully";
        } else {
            message[0] = "level" + levelNum + " is locked";
        }
        return message;
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
        if (menu == null) {
            message[0] = "invalid menu";
        } else if (menu == Menu.COIN_WALLET) {
            message[0] = "you have " + App.getCurrentUser().getCoinsCount() + " coins";
        } else if (menu == Menu.GEM_WALLET) {
            message[0] = "you have " + App.getCurrentUser().getGemsCount() + " gems";
        } else {
            App.setCurrentMenu(menu);
            message[0] = "entered " + menuSt + "successfully";
        }
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
    public static void exitGame() {
        LoginMenuController.saveLoggedInUserData();
        AppView.isRunning = false;
    }


}
