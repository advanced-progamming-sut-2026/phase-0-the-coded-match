package controllers.menus;

import enums.Commands;
import enums.Menu;
import models.App;
import views.AppView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuController {

    public static String[] showCurrentMenu(String[] message) {
        message[0] = App.getCurrentMenu().getMenuName();
        return message;
    }

    public static String enterMenu(String input) {
        Pattern pattern = Pattern.compile(Commands.ENTER_MENU.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return "invalid command";
        }
        String menuName = matcher.group("menuName");
        Menu targetMenu = App.getMenu(menuName);
        if (targetMenu == null) {
            return "invalid menu";
        }
        if (canEnter(targetMenu)) {
            App.setCurrentMenu(targetMenu);
            return "entered " + menuName;
        } else {
            return "cannot enter menu";
        }
    }

    public static void exitMenu() {
        Menu currentMenu = App.getCurrentMenu();

        if (currentMenu == Menu.SIGNUP_MENU) {
            AppView.isRunning = false;
        } else if (currentMenu == Menu.LOGIN_MENU) {
            App.setCurrentMenu(Menu.SIGNUP_MENU);
        } else if (currentMenu == Menu.MAIN_MENU) {
            System.out.println("please logout first");
        } else if (currentMenu == Menu.GAME_MENU || currentMenu == Menu.NEWS_MENU
                || currentMenu == Menu.SETTINGS_MENU || currentMenu == Menu.PROFILE_MENU) {
            App.setCurrentMenu(Menu.MAIN_MENU);
        } else if (currentMenu == Menu.COLLECTION_MENU || currentMenu == Menu.GREEN_HOUSE || currentMenu == Menu.TRAVEL_LOG
                || currentMenu == Menu.QUESTS) {
            App.setCurrentMenu(Menu.GAME_MENU);
        } else if (currentMenu == Menu.LEADERBOARD || currentMenu == Menu.BONUS_GAME) {
            App.setCurrentMenu(Menu.MAIN_MENU);
        } else if (currentMenu == Menu.MINIGAMES) {
            App.setCurrentMenu(Menu.TRAVEL_LOG);
        } else if (currentMenu == Menu.SHOP) {
            App.setCurrentMenu(Menu.GREEN_HOUSE);
        } else {
            App.setCurrentMenu(Menu.GAME_MENU);
        }
    }

    public static boolean canEnter(Menu targetMenu) {
        Menu currentMenu = App.getCurrentMenu();
        if (targetMenu == null) {
            return false;
        }
        if (targetMenu == currentMenu) return true;
        if (currentMenu == Menu.SIGNUP_MENU) {
            return targetMenu == Menu.LOGIN_MENU;
        }
        if (currentMenu == Menu.LOGIN_MENU) {
            return App.getCurrentUser() != null && targetMenu == Menu.MAIN_MENU;
        }
        if (currentMenu == Menu.MAIN_MENU) {
            return targetMenu == Menu.GAME_MENU || targetMenu == Menu.SETTINGS_MENU || targetMenu == Menu.NEWS_MENU
                    || targetMenu == Menu.PROFILE_MENU || targetMenu == Menu.LEADERBOARD || targetMenu == Menu.BONUS_GAME;
        }
        if (currentMenu == Menu.GAME_MENU) {
            return targetMenu == Menu.COLLECTION_MENU || targetMenu == Menu.GREEN_HOUSE || targetMenu == Menu.TRAVEL_LOG || targetMenu == Menu.LEADERBOARD || targetMenu == Menu.QUESTS || targetMenu == Menu.MINIGAMES;
        }
        if (currentMenu == Menu.GREEN_HOUSE) return targetMenu == Menu.SHOP;
        return false;
    }
}
