package models;

import controllers.GameManagerController;
import enums.Menu;
import enums.Phases;
import models.GameMapRelated.Lawnmower;
import models.plants.Plant;
import models.plants.PlantData;
import models.seasons.Season;
import models.zombies.Zombie;
import models.zombies.ZombieData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    private static User currentUser;
    private static User userUndergoingReset;
    private static Menu currentMenu = Menu.SIGNUP_MENU;
    private static Phases currentPhase = Phases.NORMAL_GAMEPLAY;
    private static ArrayList<User> users = new ArrayList<>();
    private static List<Season> allSeasons = new ArrayList<>();
    private static List<PlantData> allPlants = new ArrayList<>();
    private static List<ZombieData> allZombies = new ArrayList<>();
    private static List<Lawnmower> allLawnMowers = new ArrayList<>();


    public static void addPlant(PlantData plantData) {
        allPlants.add(plantData);
    }

    public static void addZombie(ZombieData zombieData) {
        allZombies.add(zombieData);
    }

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
        for( PlantData p: allPlants){
            if(p.getDisplayName().equalsIgnoreCase(plantName)){
                return p;
            }
        }
        return null;
    }

    public static void saveLoggedInUser(String username) {
        try (FileWriter writer = new FileWriter("assets/loggedInUser.txt")) {
            writer.write(username);
        } catch (IOException e) {
            System.err.println("Could not save user: " + e.getMessage());
        }
    }

    public static void loadLoggedInUser() {
        File file = new File("assets/loggedInUser.txt");
        if (!file.exists()) {
            setCurrentUser(null);
            return;
        }
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNext()) {
                String username =  scanner.next();
                User user = getUserByUsername(username);
                if (user != null) {
                    setCurrentUser(user);
                    setCurrentMenu(Menu.MAIN_MENU);
                    return;
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read file: " + e.getMessage());
        }
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
            System.out.println( "The zombie ate your brain; LOSER!!!");
            GameManagerController.getInstance().gameOver();
            return;
        }
        mower.setHasBeenUsed(true);
        //TODO: print "The lawn mower in the row <r>is triggered and killed these zombies:"
        System.out.println("The lawn mower in the row <r>is triggered and killed these zombies:");
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

    public static Lawnmower lawnMowerUsed(int row){
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

    public static List<Plant> getLockedPlants() {
        List<Plant> lockedPlants = new ArrayList<>();
        List<Plant> unlockedPlants = currentUser.getCollection().getAvailablePlants();
        for (Plant plant : allPlants) {
            if (!unlockedPlants.contains(plant)) {
                lockedPlants.add(plant);
            }
        }
        return lockedPlants;
    }
}