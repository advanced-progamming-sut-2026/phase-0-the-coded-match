package controllers;

import enums.Menu;
import models.App;

public abstract class MenuController {

    public static String[] showCurrentMenu(String[] message) {
        message[0] = App.getCurrentMenu().getMenuName();
        return message;
    }

    protected abstract void enterMenu(String targetMenu);

    protected abstract void exitMenu();
}
