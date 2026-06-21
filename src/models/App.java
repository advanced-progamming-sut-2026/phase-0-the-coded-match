package models;

import enums.Menu;
import enums.Phases;
import models.GameMapRelated.Lawnmower;
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
    private static List<Lawnmower> allLawnMowers = new ArrayList<>();

    public static Menu getCurrentMenu() {
        return currentMenu;
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

    public static boolean doesUsernameExist(String username) {
        return doesUsernameExists(username);
    }

    public static User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
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

    public static Plant getFrontMostPlantInRow(double row){
        Plant front= null;
        int minCol = Integer.MAX_VALUE;
        for(Plant p : allPlants){
            if(p.getX() == row && p.getY() < minCol){
                minCol = p.getY();
                front = p;
            }
        }
        return front;
    }

    public static void removePlant(Plant plant){
        allPlants.remove(plant);
    }

    public static void handleLawnMower(Zombie zombie){
        int row = zombie.getY();
        Lawnmower mower = lawnMowerUsed(row);
        if(mower == null){
            //TODO: needs to print "The zombie ate your brain; LOSER!!!" how do we send this to view?
            //TODO: GAME OVER (needs an overall method in the game)
            return;
        }
        mower.setHasBeenUsed(true);
        //TODO: print "The lawn mower in the row <r>is triggered and killed these zombies:"
        List<Zombie> killed = new ArrayList<>();
        for(Zombie z : allZombies){
            if(z.getY() == row){ //Todo: Make an exception for BOSS ZOMBIE!!!!!
                killed.add(z);
                z.setCurrentHp(0);
                //TODO: PRINT "Zombie of type <type> is dead at (<x>, y>)"
                //TODO: i think we should erase these dead zombies from the game? a new method perhaps?
            }
        }

        for(Zombie z : killed){
            // TODO: Print the zombie.getType() of that row that are now dead;
        }

    }

    private static Lawnmower lawnMowerUsed(int row){
        for(Lawnmower mower: allLawnMowers){
            if (mower.getRow() != row){
                continue;
            }
            if (mower.HasBeenUsed()){
                return null;
            }
            return mower;
        }
        return null;
    }
}