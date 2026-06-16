package controllers;

import enums.Menu;
import models.App;

public class MainMenuController {

    public static void logout() {
        if (App.getCurrentUser() != null) {
            App.getCurrentUser().setStayLoggedIn(false);
        }
        App.setCurrentUser(null);
        App.setCurrentMenu(Menu.SIGNUP_MENU);
        System.out.println("logged out successfully");
    }
}