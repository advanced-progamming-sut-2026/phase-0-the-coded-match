package controllers.menus;

import controllers.GameManagerController;
import enums.Commands;
import enums.Menu;
import models.App;
import models.Level;
import models.LevelData;
import models.seasons.Season;
import models.plants.PlantData;
import models.plants.PlantRepository;
import models.specialLevels.ConveyorBeltStrategy;
import views.AppView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameMenuController {
    private static Season selectedSeason;

    public static String[] enterSeason(String input, String[] message) {
        Matcher matcher = Pattern.compile(Commands.ENTER_SEASON.getPattern()).matcher(input);
        if (!matcher.matches()) {
            message[0] = "Invalid command";
            return message;
        }
        String seasonName = matcher.group("season").trim();
        Season season = App.getSeason(seasonName);
        if (season == null) {
            message[0] = "season does not exist";
        } else if (!season.isUnlocked()) {
            message[0] = seasonName + " is locked";
        } else {
            selectedSeason = season;
            if (App.getCurrentUser() != null && season.getData().getUnlockedPlants() != null) {
                for (String plantName : season.getData().getUnlockedPlants()) {
                    PlantData plant = PlantRepository.getInstance().findByName(plantName);
                    if (plant != null) {
                        App.getCurrentUser().getCollection().unlockPlant(plant.getId());
                    }
                }
            }
            message[0] = "entered " + season.getName() + " successfully";
        }
        return message;
    }

    public static String[] enterLevel(String input, String[] message) {
        Matcher matcher = Pattern.compile(Commands.ENTER_LEVEL.getPattern()).matcher(input);
        if (!matcher.matches()) {
            message[0] = "Invalid command";
            return message;
        }
        if (selectedSeason == null) {
            message[0] = "enter a season first";
            return message;
        }
        int levelNum = Integer.parseInt(matcher.group("level"));
        LevelData data = App.getLevelByNumber(levelNum, selectedSeason);
        if (data == null) {
            message[0] = "level does not exist";
        } else if (!data.isUnlocked()) {
            message[0] = "level " + levelNum + " is locked";
        } else {
            Level level = new Level(data);
            level.setCurrentSeason(selectedSeason);
            GameManagerController.getInstance().setCurrentLevel(level);
            if (level.getSpecialLevel() instanceof ConveyorBeltStrategy) {
                ChoosePlantsMenuController.startGame();
            } else {
                App.setCurrentMenu(Menu.CHOOSEPLANTS_MENU);
            }
            message[0] = "entered level " + levelNum + " successfully";
        }
        return message;
    }

    public static String[] enter(String input, String[] message) {
        Matcher matcher = Pattern.compile(Commands.GAME_MENU_MENUS.getPattern()).matcher(input);
        if (!matcher.matches()) {
            message[0] = "Invalid command";
            return message;
        }
        String menuName = matcher.group("menu");
        Menu menu = App.getMenu(menuName);
        if (menu == null) {
            message[0] = "invalid menu";
        } else if (menu == Menu.COIN_WALLET) {
            message[0] = "you have " + App.getCurrentUser().getCoinsCount() + " coins";
        } else if (menu == Menu.GEM_WALLET) {
            message[0] = "you have " + App.getCurrentUser().getGemsCount() + " gems";
        } else {
            App.setCurrentMenu(menu);
            message[0] = "entered " + menuName + " successfully";
        }
        return message;
    }

    public static String[] cheatAddCoinOrGem(String input, String[] message) {
        Matcher matcher = Pattern.compile(Commands.CHEAT_ADD_CURRENCY.getPattern()).matcher(input);
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
        message[0] = amount + " " + currency + " added successfully";
        return message;
    }

    public static void exitGame() {
        SignupMenuController.saveToJson();
        App.saveLoggedInUser(App.getCurrentUser().getUsername());
        AppView.isRunning = false;
    }
}
