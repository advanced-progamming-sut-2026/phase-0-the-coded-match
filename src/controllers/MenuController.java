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
        if (menuName != null) {
            Menu targetMenu = App.getMenu(menuName);
            if (canEnter(targetMenu)) { //TODO: check possible errors?
                App.setCurrentMenu(targetMenu);
            }
        }
    }

    public static void exitMenu() {
        Menu currentMenu = App.getCurrentMenu();

        if (currentMenu == Menu.SIGNUP_MENU) {
            AppView.isRunning = false;
        } else if (currentMenu == Menu.LOGIN_MENU) {
            App.setCurrentMenu(Menu.SIGNUP_MENU);
        } else if (currentMenu == Menu.MAIN_MENU) {
            MainMenuController.logout();
        } else if (currentMenu == Menu.GAME_MENU || currentMenu == Menu.NEWS_MENU
                || currentMenu == Menu.SETTINGS_MENU || currentMenu == Menu.PROFILE_MENU) { //TODO: and network menu
            App.setCurrentMenu(Menu.MAIN_MENU);
        } else {
            App.setCurrentMenu(Menu.GAME_MENU);
        }
    }

    public static boolean canEnter(Menu targetMenu) {
        Menu currentMenu = App.getCurrentMenu();
        if (currentMenu == Menu.SIGNUP_MENU && targetMenu != Menu.LOGIN_MENU) {
            return false;
        } else if () {

        }
    }
}
