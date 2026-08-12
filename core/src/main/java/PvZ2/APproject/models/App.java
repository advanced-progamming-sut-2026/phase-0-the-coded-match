package PvZ2.APproject.models;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.controllers.QuestController;
import PvZ2.APproject.controllers.SeasonController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.enums.Phases;
import PvZ2.APproject.models.GameMapRelated.Lawnmower;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;
import PvZ2.APproject.models.seasons.Season;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.ZombieData;
import PvZ2.APproject.models.zombies.ZombieRepository;

import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class App {
    private static User currentUser;
    private static User userUndergoingReset;
    private static Menu currentMenu = Menu.SIGNUP_MENU;
    private static Phases currentPhase = Phases.NORMAL_GAMEPLAY;
    private static ArrayList<User> users = new ArrayList<>();
    private static List<Season> allSeasons = new ArrayList<>();
    private static List<ZombieData> allZombies = new ArrayList<>();
    private static List<Lawnmower> allLawnMowers = new ArrayList<>();

    public static void initialize() {
        allSeasons = new ArrayList<>(SeasonController.getInstance().getActiveSeasons());
        for (User user : users) user.ensureDefaults();
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

    public static void setCurrentUser(User currentUser) { App.currentUser = currentUser; if (currentUser != null) currentUser.ensureDefaults(); }

    public static void setUserUndergoingReset(User user){
        userUndergoingReset = user;
    }

    public static User getUserUndergoingReset(){
        return userUndergoingReset;
    }

    public static void setCurrentMenu(Menu currentMenu) { if (currentMenu != null) App.currentMenu = currentMenu; }

    public static Phases getCurrentPhase() {
        return currentPhase;
    }

    public static void setCurrentPhase(Phases currentPhase) {
        App.currentPhase = currentPhase;
    }

    public static ArrayList<User> getUsers(){
        return users;
    }

    public static void setUsers(ArrayList<User> users) { App.users = users == null ? new ArrayList<>() : users; }

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
        if (username != null) for (User user : users) if (username.equalsIgnoreCase(user.getUsername())) return user;
        return null;
    }

    public static void addUser(User user) {
        users.add(user);
    }

    public static LevelData getLevelByNumber(int num, Season season) {
        if (season == null) return null;
        for (LevelData level : season.getLevels()) {
            if (level.getLevelNumber() == num) {
                return level;
            }
        }
        return null;
    }

    public static Menu getMenu(String menuName) {
        if (menuName == null) return null;
        menuName = menuName.trim();
        for (Menu menu : Menu.values()) {
            if (menu.getMenuName().equalsIgnoreCase(menuName)) {
                return menu;
            }
        }
        return null;
    }

    public static Season getSeason(String seasonName) {
        if (seasonName == null) return null;
        seasonName = seasonName.trim();
        for (Season season : allSeasons) {
            if (season.getType().getName().equalsIgnoreCase(seasonName) || season.getName().equalsIgnoreCase(seasonName) || season.getType().name().equalsIgnoreCase(seasonName.replace(" ", "_"))) {
                return season;
            }
        }
        return null;
    }

    public static void saveLoggedInUser(String username) {
        try {
            Gdx.files.local("loggedInUser.txt").writeString(username, false);
        } catch (Exception e) {
            System.err.println("Could not save user: " + e.getMessage());
        }
    }

    public static void clearLoggedInUser() { saveLoggedInUser(""); }

    public static void loadLoggedInUser() {
        FileHandle file = Gdx.files.local("loggedInUser.txt");

        if (!file.exists()) {
            setCurrentUser(null);
            return;
        }

        try {
            String username = file.readString().trim();

            if (!username.isEmpty()) {
                User user = getUserByUsername(username);
                if (user != null) {
                    setCurrentUser(user);
                    setCurrentMenu(Menu.MAIN_MENU);
                    return;
                }
            }

            setCurrentUser(null);
        } catch (Exception e) {
            System.err.println("Could not read file: " + e.getMessage());
            setCurrentUser(null);
        }
    }

    public static void initializeLawnMowers(int rows) { allLawnMowers.clear(); for (int row = 1; row <= rows; row++) allLawnMowers.add(new Lawnmower(row)); }

    public static void handleLawnMower(Zombie zombie){
        int row = zombie.getY();
        Lawnmower mower = lawnMowerUsed(row);
        if(mower == null){

            System.out.println( "The zombie ate your brain; LOSER!!!");
            GameManagerController.getInstance().gameOver();
            return;
        }
        mower.setHasBeenUsed(true);

        System.out.println("The lawn mower in the row " + row + " is triggered and killed these zombies:");
        List<Zombie> killed = new ArrayList<>();
        List<Zombie> activeZombies = GameManagerController.getInstance().getCurrentLevel().getActiveZombies();
        for (Zombie zombieInRow : new ArrayList<>(activeZombies)) {
            if (zombieInRow.getY() == row) {
                killed.add(zombieInRow);
                zombieInRow.setCurrentHp(0);
            }
        }
        QuestController.notifyZombiesKilledByLawnmower(killed.size());

        for (Zombie z : killed) System.out.println(z.getData().getDisplayName());

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

    public static List<PlantData> getLockedPlants() {
        List<PlantData> lockedPlants = new ArrayList<>();
        List<String> unlockedPlants = currentUser == null ? List.of() : currentUser.getCollection().getAvailablePlantsIds();
        for (PlantData plant : PlantRepository.getInstance().getAllPlants()) {
            if (!unlockedPlants.contains(plant.getId())) {
                lockedPlants.add(plant);
            }
        }
        return lockedPlants;
    }
}
