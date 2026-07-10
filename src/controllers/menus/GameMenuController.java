package controllers.menus;

import controllers.GameManagerController;
import enums.Commands;
import enums.Menu;
import models.App;
import models.Level;
import models.LevelData;
import models.seasons.Season;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameMenuController{

    public static void enterSeason(String input) {
        Pattern pattern = Pattern.compile(Commands.ENTER_SEASON.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return;
        }
        String seasonName = matcher.group("season");
        Season season = App.getSeason(seasonName);
        App.getCurrentUser().setLastSeason(season);
    }

    public static void enterLevel(String input) {
        Pattern pattern = Pattern.compile(Commands.ENTER_LEVEL.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return;
        }
        int levelNum = Integer.parseInt(matcher.group("level"));
        LevelData level = App.getLevelByNumber(levelNum, App.getCurrentUser().getLastSeason());
        if (level.isUnlocked()) {
            App.getCurrentUser().setLastLevel(level);
            Level currentLevel = new Level(level);
            GameManagerController.getInstance().setCurrentLevel(currentLevel);

        }
    }

    public static void enter(String input) {
        Pattern pattern = Pattern.compile(Commands.GAME_MENU_MENUS.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return;
        }
        String menuSt = matcher.group("menu");
        Menu menu = App.getMenu(menuSt);
        App.setCurrentMenu(menu);
    }

    public static void cheatAddCoinOrGem(String input) {
        Pattern pattern = Pattern.compile(Commands.CHEAT_ADD_CURRENCY.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return;
        }
        int amount = Integer.parseInt(matcher.group("amount"));
        String currency = matcher.group("currency");
        if (currency.equals("coin")) {
            App.getCurrentUser().setCoinsCount(App.getCurrentUser().getCoinsCount() + amount);
        } else {
            App.getCurrentUser().setGemsCount(App.getCurrentUser().getGemsCount() + amount);
        }
    }
}
