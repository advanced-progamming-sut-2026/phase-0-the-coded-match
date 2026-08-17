package PvZ2.APproject.controllers.menus;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.LevelData;
import PvZ2.APproject.models.seasons.Season;
import PvZ2.APproject.views.AppView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameMenuController{

    public static void enterSeason(String input) {
        Season season = App.getSeason(input);

        if (season == null) {
          System.out.println("debug szn does not exist");
        } else if (season.isUnlocked()) {
            App.getCurrentUser().setLastSeason(season);
        } else {
            System.out.println(input+" is locked");
        }
    }

    public static void enterLevel(int levelNumber) {

        LevelData level = App.getLevelByNumber(levelNumber, App.getCurrentUser().getLastSeason());

        if (level == null) {
           System.out.println("oops no level");
        } else if (level.isUnlocked()) {
            App.getCurrentUser().setLastLevel(level);
            Level currentLevel = new Level(level);
            currentLevel.setCurrentSeason(App.getCurrentUser().getLastSeason());
            GameManagerController.getInstance().setCurrentLevel(currentLevel);
            App.setCurrentMenu(Menu.CHOOSEPLANTS_MENU);
        } else {
            System.out.println("level" + levelNumber + " is locked"); ;
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
//    public static void exitGame() {
//        App.saveLoggedInUser();
//        AppView.isRunning = false;
//    }


}
