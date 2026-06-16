package models;

import enums.Menu;
import enums.Phases;
import models.plants.Plant;
import models.seasons.Season;
import models.zombies.Zombie;

import java.util.ArrayList;
import java.util.List;

public class App {
    private static Menu currentMenu = Menu.SIGNUP_MENU;
    private static ArrayList<User> users = new ArrayList<>();
    private static User currentUser;
    private static List<Season> allSeasons = new ArrayList<>();
    private static Phases currentPhase = Phases.NORMAL_GAMEPLAY;
    private static User userUndergoingReset;
    private static List<Plant> allPlants = new ArrayList<>();
    private static List<Zombie> allZombies = new ArrayList<>();

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

    public static List<Plant> getAllPlants() {
        return allPlants;
    }

    public static void setAllPlants(List<Plant> allPlants) {
        App.allPlants = allPlants;
    }

    public static List<Zombie> getAllZombies() {
        return allZombies;
    }

    public static void setAllZombies(List<Zombie> allZombies) {
        App.allZombies = allZombies;
    }

    public static void setUsers(ArrayList<User> users) {
        App.users = users;
    }

    public static List<Season> getAllSeasons() {
        return allSeasons;
    }

    public static void setAllSeasons(List<Season> allSeasons) {
        App.allSeasons = allSeasons;
    }

    public static boolean doesUsernameExists(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public static void addUser(User user) {
        users.add(user);
    }

    public static void setUserUndergoingReset(User user){
        userUndergoingReset = user;
    }

    public static User getUserUndergoingReset(){
        return userUndergoingReset;
    }

    public static LevelData getLevelByNumber(int num, Season season) {
        for (LevelData level : season.getLevels()) {
            if (level.getLevelNumber() == num) {
                return level;
            }
        }
        return null;
    }

    public static Menu getMenu(String menuName) {
        for (Menu menu : Menu.values()) {
            if (menu.getMenuName().equalsIgnoreCase(menuName)) {
                return menu;
            }
        }
        return null;
    }

    public static Season getSeason(String seasonName) {
        for (Season season : allSeasons) {
            if (season.getType().getName().equalsIgnoreCase(seasonName)) {
                return season;
            }
        }
        return null;
    }
}
