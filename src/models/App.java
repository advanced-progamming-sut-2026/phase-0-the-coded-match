package models;

import enums.Menu;
import enums.Phases;
import models.plants.Plant;
import models.seasons.Season;
import models.zombies.Zombie;

import java.util.ArrayList;
import java.util.List;

public class App {
    private static User currentUser;
    private static User userUndergoingReset;
    private static Level currentLevel;
    private static Menu currentMenu = Menu.SIGNUP_MENU;
    private static Phases currentPhase = Phases.NORMAL_GAMEPLAY;
    private static ArrayList<User> users = new ArrayList<>();
    private static List<Season> allSeasons = new ArrayList<>();
    private static List<Plant> allPlants = new ArrayList<>();
    private static List<Zombie> allZombies = new ArrayList<>();

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

    public static Menu getCurrentMenu() {
        return currentMenu;
    }

    public static void setCurrentMenu(Menu currentMenu) {
        App.currentMenu = currentMenu;
    }

    public static Level getCurrentLevel(){return  currentLevel;}

    public static Phases getCurrentPhase() {
        return currentPhase;
    }

    public static void setCurrentPhase(Phases currentPhase) {
        App.currentPhase = currentPhase;
    }

    public static ArrayList<User> getUsers(){
        return users;
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

    public static boolean doesUsernameExist(String username) {
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

    public static Plant getPlantByName(String plantName){
        for( Plant p: allPlants){
            if(p.getData().getDisplayName().equalsIgnoreCase(plantName)){
                return p;
            }
        }
        return null;
    }
}
