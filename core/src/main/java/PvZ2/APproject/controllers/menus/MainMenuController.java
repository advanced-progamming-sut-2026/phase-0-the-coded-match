package PvZ2.APproject.controllers.menus;

import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;

public class MainMenuController {

    public static void logout() {
        if (App.getCurrentUser() != null) {
            App.getCurrentUser().setStayLoggedIn(false);
        }
        SignupMenuController.saveToJson();
        App.clearLoggedInUser();
        App.setCurrentUser(null);
        App.setCurrentMenu(Menu.SIGNUP_MENU);
        System.out.println("logged out successfully");
    }
}
