package controllers.menus;

import enums.Menu;
import models.App;

import java.io.File;

public class MainMenuController {

    public static void logout() {
        if (App.getCurrentUser() != null) {
            App.getCurrentUser().setStayLoggedIn(false);
        }
        App.setCurrentUser(null);
        App.setCurrentMenu(Menu.SIGNUP_MENU);
        File file = new File("assets.Data/loggedInUser.txt");
        if (file.exists()) {
            file.delete();
        }
        System.out.println("logged out successfully");
    }
}