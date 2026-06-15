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

    public void setCurrentMenu(Menu currentMenu) {
        this.currentMenu = currentMenu;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public static boolean doesUsernameExists(String username) {

    }
}
