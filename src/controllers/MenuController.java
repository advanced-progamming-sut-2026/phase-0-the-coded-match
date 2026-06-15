package controllers;

import enums.Commands;
import enums.Menu;
import models.App;

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
        String menuName = matcher.group("menuName");
        if (menuName != null) {
            Menu targetMenu = getMenuEnum(menuName);
            if (canEnter(targetMenu)) { //TODO: check possible errors?
                App.setCurrentMenu(targetMenu);
            }
        }
    }

    public static void exitMenu() {

    }

    public static Menu getMenuEnum(String menuName) {
        for (Menu menu : Menu.values()) {
            if (menu.getMenuName().equalsIgnoreCase(menuName)) {
                return menu;
            }
        }
        return null;
    }

    public static boolean canEnter(Menu targetMenu) {
        Menu currentMenu = App.getCurrentMenu();
        if (currentMenu == Menu.SIGNUP_MENU && targetMenu != Menu.LOGIN_MENU) {
            return false;
        } else if () {

        }
    }
}
