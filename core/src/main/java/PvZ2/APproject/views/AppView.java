package PvZ2.APproject.views;

import PvZ2.APproject.controllers.menus.MenuController;
import PvZ2.APproject.controllers.menus.SignupMenuController;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.views.menus.*;

import java.util.Scanner;

public class AppView {
    public static String[] message = new String[1];
    public static boolean isRunning = true;

    public static void run() {
        SignupMenuController.loadFromJson();
        App.initialize();
        App.loadLoggedInUser();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine() && isRunning) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            Menu currentMenu = App.getCurrentMenu();

            if (input.matches(Commands.SHOW_MENU.getPattern())) {
                MenuController.showCurrentMenu(message);
                System.out.println(message[0]);
            } else if (input.matches(Commands.ENTER_MENU.getPattern()) && !input.matches(Commands.ENTER_SEASON.getPattern()) && !input.matches(Commands.ENTER_LEVEL.getPattern())) {
                System.out.println(MenuController.enterMenu(input));
            } else if (input.matches(Commands.EXIT_MENU.getPattern())) {
                MenuController.exitMenu();
            } else if (currentMenu == Menu.SIGNUP_MENU) {
                SignupMenu.check(input, scanner);
            } else if (currentMenu == Menu.LOGIN_MENU) {
                LoginMenu.check(input);
            } else if (currentMenu == Menu.MAIN_MENU) {
                MainMenu.check(input);
            } else if (currentMenu == Menu.GAME_MENU) {
                GameMenu.check(input);
            } else if (currentMenu == Menu.SETTINGS_MENU) {
                SettingsMenu.check(input);
            } else if (currentMenu == Menu.NEWS_MENU) {
                NewsMenu.check(input);
            } else if (currentMenu == Menu.PROFILE_MENU) {
                ProfileMenu.check(input);
            } else if (currentMenu == Menu.COLLECTION_MENU) {
                CollectionMenu.check(input);
            } else if (currentMenu == Menu.CHOOSEPLANTS_MENU) {
                ChoosePlantsMenu.check(input);
            } else if (currentMenu == Menu.GAME_MANAGER) {
                GameManager.check(input);
            } else if (currentMenu == Menu.GREEN_HOUSE) {
                GreenHouseView.check(input);
            } else if (currentMenu == Menu.TRAVEL_LOG) {
                TravelLogView.check(input);
            } else if (currentMenu == Menu.LEADERBOARD) {
                LeaderBoardView.check(input);
            } else if (currentMenu == Menu.BONUS_GAME) {
                BonusGameView.check(input);
            } else if (currentMenu == Menu.SHOP) {
                ShopView.check(input);
            } else if (currentMenu == Menu.QUESTS) {
                QuestView.check(input);
            } else if (currentMenu == Menu.MINIGAMES) {
                MiniGameView.check(input);
            } else {
                System.out.println("invalid command");
            }
        }
    }
}
