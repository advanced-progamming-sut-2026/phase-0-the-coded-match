package controllers;

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

    public static void enterMenu(String input) {
        Pattern pattern = Pattern.compile(Commands.ENTER_MENU.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return;
        }
        String menuName = matcher.group("menu_name");
        Menu targetMenu = App.getMenu(menuName);
        if (targetMenu == null) {
            System.out.println("invalid menu");
            return;
        }
        if (canEnter(targetMenu)) {
            App.setCurrentMenu(targetMenu);
        } else {
            System.out.println("cannot enter menu");
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
        } else if (currentMenu == Menu.COLLECTION_MENU) {
            App.setCurrentMenu(Menu.GAME_MENU);
        } else {
            App.setCurrentMenu(Menu.GAME_MENU);
        }
    }

    public static boolean canEnter(Menu targetMenu) {
        Menu currentMenu = App.getCurrentMenu();
        if (targetMenu == null) {
            return false;
        }
        if (currentMenu == Menu.SIGNUP_MENU) {
            return targetMenu == Menu.LOGIN_MENU;
        }
        if (currentMenu == Menu.LOGIN_MENU) {
            return App.getCurrentUser() != null && targetMenu == Menu.MAIN_MENU;
        }
        if (currentMenu == Menu.MAIN_MENU) {
            return targetMenu == Menu.GAME_MENU || targetMenu == Menu.SETTINGS_MENU || targetMenu == Menu.NEWS_MENU
                    || targetMenu == Menu.PROFILE_MENU;
        }
        if (currentMenu == Menu.GAME_MENU) {
            return targetMenu == Menu.COLLECTION_MENU;
        }
        return false;
    }
}
