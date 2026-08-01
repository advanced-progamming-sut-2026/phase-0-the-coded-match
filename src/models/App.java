package models;

import controllers.GameManagerController;
import controllers.QuestController;
import enums.Menu;
import enums.Phases;
import models.GameMapRelated.Lawnmower;
import models.plants.Plant;
import models.plants.PlantData;
import models.plants.PlantRepository;
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
    private static List<ZombieData> allZombies = new ArrayList<>();
    private static List<Lawnmower> allLawnMowers = new ArrayList<>();

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
        if (seasonName == null) {
            return null;
        }
        String normalized = seasonName.trim().replace('_', ' ');
        for (Season season : allSeasons) {
            if (season.getType().getName().equalsIgnoreCase(normalized)
                    || season.getName().equalsIgnoreCase(normalized)
                    || season.getData().getName().equalsIgnoreCase(normalized)
                    || season.getType().name().replace('_', ' ').equalsIgnoreCase(normalized)) {
                return season;
            }
        }
        return null;
    }

    private static File resolveAssetFile(String name) {
        File sourceAssets = new File("src/assets");
        File assets = new File("assets");
        File directory = sourceAssets.isDirectory() ? sourceAssets : assets;
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return new File(directory, name);
    }

    public static void saveLoggedInUser(String username) {
        try (FileWriter writer = new FileWriter(resolveAssetFile("loggedInUser.txt"))) {
            writer.write(username);
        } catch (IOException e) {
            System.err.println("Could not save user: " + e.getMessage());
        }
    }

    public static void loadLoggedInUser() {
        File file = resolveAssetFile("loggedInUser.txt");
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
                    QuestController.initializeForCurrentUser();
                    QuestController.onLevelStarted();
                    return;
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read file: " + e.getMessage());
        }
    }

    public static void handleLawnMower(Zombie zombie) {
        int row = zombie.getY();
        Lawnmower mower = lawnMowerUsed(row);
        if (mower == null) {
            System.out.println("The zombie ate your brain; LOSER!!!");
            GameManagerController.getInstance().gameOver();
            return;
        }
        mower.setHasBeenUsed(true);
        StringBuilder killed = new StringBuilder();
        int killedCount = 0;
        for (Zombie zombieInRow : GameManagerController.getInstance().getCurrentLevel().getActiveZombies()) {
            if (zombieInRow.getY() == row && !zombieInRow.isDead() && !zombieInRow.isBoss()) {
                if (killed.length() > 0) {
                    killed.append(", ");
                }
                killed.append(zombieInRow.getData().getDisplayName()).append("@(")
                        .append(String.format("%.2f", zombieInRow.getX())).append(", ")
                        .append(zombieInRow.getY()).append(')');
                zombieInRow.setCurrentHp(0);
                killedCount++;
            }
        }
        System.out.println("The lawn mower in the row " + row + " is triggered and killed these zombies:");
        System.out.println(killed.length() == 0 ? "none" : killed);
        if (QuestController.isReady()) {
            QuestController.notifyZombiesKilledByLawnmower(killedCount);
        }
    }

    public static Lawnmower lawnMowerUsed(int row) {
        if (GameManagerController.getInstance().getCurrentLevel() == null
                || GameManagerController.getInstance().getCurrentLevel().getGameMap() == null) {
            return null;
        }
        for (Lawnmower mower : GameManagerController.getInstance().getCurrentLevel().getGameMap().getLawnmowers()) {
            if (mower.getRow() == row && !mower.HasBeenUsed()) {
                return mower;
            }
        }
        return null;
    }

    public static List<PlantData> getLockedPlants() {
        List<PlantData> lockedPlants = new ArrayList<>();
        List<String> unlockedPlants = currentUser.getCollection().getAvailablePlantsIds();
        for (PlantData plant : PlantRepository.getInstance().getAllPlants()) {
            if (!unlockedPlants.contains(plant.getId())) {
                lockedPlants.add(plant);
            }
        }
        return lockedPlants;
    }
}
