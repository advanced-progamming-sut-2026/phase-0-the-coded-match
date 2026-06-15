package models;

import controllers.ZombieWaveManager;
import enums.Menu;

import java.util.ArrayList;
import java.util.List;

public class App {
    private static Menu currentMenu = Menu.SIGNUP_MENU;
    private ArrayList<User> users;
    private User currentUser;

    public static Menu getCurrentMenu() {
        return currentMenu;
    }

    public static void setCurrentMenu(Menu currentMenu) {
        this.currentMenu = currentMenu;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public static boolean doesUsernameExists(String username) {

    }
}
