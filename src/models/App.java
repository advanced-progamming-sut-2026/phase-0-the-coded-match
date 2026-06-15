package models;

import controllers.ZombieWaveManager;
import enums.Menu;
import enums.Phases;

import java.util.ArrayList;
import java.util.List;

public class App {
    private static Menu currentMenu = Menu.SIGNUP_MENU;
    private static Phases currentPhase = Phases.NORMAL_GAMEPLAY;
    private static ArrayList<User> users;
    private static User currentUser;
    private static User userUndergoingReset;

    public static Menu getCurrentMenu() {
        return currentMenu;
    }

    public static Phases getCurrentPhase() {
        return currentPhase;
    }

    public static void setCurrentPhase(Phases currentPhase) {
        App.currentPhase = currentPhase;
    }

    public static ArrayList<User> getUsers(){
        return users;
    }

    public static void setCurrentMenu(Menu currentMenu) {
        App.currentMenu = currentMenu;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        App.currentUser = currentUser;
    }

    public static void setUserUndergoingReset(User user){
        userUndergoingReset = user;
    }

    public static User getUserUndergoingReset(){
        return userUndergoingReset;
    }
}
