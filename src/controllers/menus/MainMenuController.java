package controllers.menus;

import enums.Menu;
import models.App;

import java.io.File;

public class MainMenuController {

    public static void logout() {
        if (App.getCurrentUser() != null) {
            App.getCurrentUser().setStayLoggedIn(false);
        }
        LoginMenuController.clearPlayer();
        App.setCurrentUser(null);
        App.setCurrentMenu(Menu.SIGNUP_MENU);
        System.out.println("logged out successfully");
    }
}