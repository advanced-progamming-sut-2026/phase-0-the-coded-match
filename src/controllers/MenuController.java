package controllers;

public abstract class MenuController {

    protected void showCurrentMenu() {

    }

    protected abstract void enterMenu(String targetMenu);

    protected abstract void exitMenu();
}
