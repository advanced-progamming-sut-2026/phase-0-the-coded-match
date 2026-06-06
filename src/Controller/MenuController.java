package controllers;

public abstract class MenuController {
    protected abstract void handleCommand(String input);

    protected void showCurrentMenu() {

    }

    protected abstract void enterMenu(String targetMenu);

    protected abstract void exitMenu();
}
