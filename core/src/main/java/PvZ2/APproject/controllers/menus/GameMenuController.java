package PvZ2.APproject.controllers.menus;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.LevelData;
import PvZ2.APproject.models.seasons.Season;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameMenuController{
    private static Season selectedSeason;

    public static void enterSeason(String input) {
        Season season = App.getSeason(input);

        if (season == null) {
          System.out.println("debug szn does not exist");
        } else if (season.isUnlocked()) {
            selectedSeason = season;
            System.out.println("entered season");
        } else {
            System.out.println(input+" is locked");
        }
    }

    public static boolean enterLevel(int levelNumber) {
        Season season = selectedSeason;
        if (season == null) season = App.getCurrentUser() == null ? null : App.getCurrentUser().getLastSeason();
        LevelData level = App.getLevelByNumber(levelNumber, season);

        if (level == null) {
            System.out.println("oops no level");
            return false;
        } else if (level.isUnlocked()) {
            Level currentLevel = new Level(level);
            currentLevel.setCurrentSeason(season);
            GameManagerController.getInstance().setCurrentLevel(currentLevel);
            if (season != null) season.levelStarted(currentLevel);
            App.setCurrentMenu(Menu.CHOOSEPLANTS_MENU);
            return true;
        } else {
            System.out.println("level" + levelNumber + " is locked");
            return false;
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

    public static void cheatAddCoinOrGem(int amount, String currency) {

        if (currency.equalsIgnoreCase("coin")) {
            App.getCurrentUser().addCoins(amount);
        } else {
            App.getCurrentUser().addGems(amount);
        }
    }
//    public static void exitGame() {
//        App.saveLoggedInUser();
//        AppView.isRunning = false;
//    }


}
