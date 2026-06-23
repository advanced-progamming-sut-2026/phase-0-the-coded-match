package views;

import controllers.MenuController;
import controllers.SignupMenuController;
import enums.Commands;
import enums.Menu;
import models.App;
import models.User;

import java.util.Scanner;

public class AppView {
    public static String[] message = new String[1];
    public static boolean isRunning = true;

    public static void run() {
        SignupMenuController.loadFromJson();
        App.loadLoggedInUser();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine() && isRunning) {
            String input = scanner.nextLine();
            if (input.matches(Commands.SHOW_MENU.getPattern())) {
                MenuController.showCurrentMenu(message);
                System.out.println(message[0]);
            } else if (input.matches(Commands.ENTER_MENU.getPattern())) {
                MenuController.enterMenu(input);
            } else if (input.matches(Commands.EXIT_MENU.getPattern())) {
                MenuController.exitMenu();
            } else if (App.getCurrentMenu() == Menu.SIGNUP_MENU) {
                SignupMenu.check(input, scanner);
            } else if (App.getCurrentMenu() == Menu.LOGIN_MENU) {
                LoginMenu.check(input);
            } else if (App.getCurrentMenu() == Menu.MAIN_MENU) {
                MainMenu.check(input);
            } else if (App.getCurrentMenu() == Menu.GAME_MENU) {
                GameMenu.check(input);
            } else if (App.getCurrentMenu() == Menu.SETTINGS_MENU) {
                SettingsMenu.check(input);
            } else if (App.getCurrentMenu() == Menu.NEWS_MENU) {
                NewsMenu.check(input);
            } else if (App.getCurrentMenu() == Menu.PROFILE_MENU) {
                ProfileMenu.check(input);
            } else if (App.getCurrentMenu() == Menu.COLLECTION_MENU) {
                CollectionMenu.check(input);
            } else {
                System.out.println("invalid command");
            }
        }
    }
}
