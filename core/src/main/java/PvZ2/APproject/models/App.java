package PvZ2.APproject.models;

import PvZ2.APproject.client.MessageType;
import PvZ2.APproject.client.NetworkClient;
import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.controllers.QuestController;
import PvZ2.APproject.controllers.SeasonController;
import PvZ2.APproject.controllers.menus.SignupMenuController;
import PvZ2.APproject.enums.Gender;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.enums.Phases;
import PvZ2.APproject.models.GameMapRelated.Lawnmower;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;
import PvZ2.APproject.models.seasons.Season;
import PvZ2.APproject.models.seasons.SeasonData;
import PvZ2.APproject.models.seasons.SeasonRepository;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.ZombieData;
import PvZ2.APproject.models.zombies.ZombieRepository;

import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class App {
    private static User currentUser;
    private static NetworkClient networkClient;
    private static String resetToken;
    private static String resetUsername;
    private static User userUndergoingReset;
    private static Menu currentMenu = Menu.SIGNUP_MENU;
    private static Phases currentPhase = Phases.NORMAL_GAMEPLAY;
    private static ArrayList<User> users = new ArrayList<>();
    private static List<Season> allSeasons = new ArrayList<>();
    private static List<ZombieData> allZombies = new ArrayList<>();
    private static List<Lawnmower> allLawnMowers = new ArrayList<>();
    private static final Map<Season, Boolean> defaultSeasonUnlocks = new IdentityHashMap<>();
    private static final Map<LevelData, Boolean> defaultLevelUnlocks = new IdentityHashMap<>();


/// Phase 3 implementation ///
    /** One shared TCP connection is used by all client controllers. */
    public static synchronized NetworkClient getNetworkClient() {
        if (networkClient == null) networkClient = new NetworkClient();
        return networkClient;
    }

    public static synchronized void connectToServer() throws IOException {
        NetworkClient client = getNetworkClient();
        if (!client.isConnected()) client.connect();
        client.setPushListener(PvZ2.APproject.controllers.MiniGameController::handleNetworkResponse);
    }

    public static synchronized void disconnectFromServer() {
        if (networkClient != null) networkClient.disconnect();
    }

    /**
     * Serializes the complete persistent User object, except for the password.
     * The JSON file remains a local backup/migration source; the server copy is
     * the persistent account source used after login.
     */
    public static String exportCurrentUserState() {
        if (currentUser == null) return "";
        Gson gson = new Gson();
        JsonObject root = gson.toJsonTree(currentUser).getAsJsonObject();
        root.remove("password");
        root.remove("stayLoggedIn");
        return gson.toJson(root);
    }

    public static String syncCurrentUserToServer() {
        if (currentUser == null || networkClient == null || !networkClient.isConnected()) return "";
        try {
            Request request = new Request(MessageType.SYNC_USER_STATE);
            request.put("stateJson", exportCurrentUserState());
            Response response = networkClient.sendAndWait(request);
            return response.getMessage();
        } catch (Exception e) {
            System.err.println("Could not synchronize user state: " + e.getMessage());
            return "";
        }
    }

    /** Applies the server copy after a successful login. */
    public static void applyServerUserState(Map<String, String> data, String loginPassword) {
        if (data == null) return;
        String stateJson = data.get("stateJson");
        if (stateJson != null && !stateJson.isBlank()) {
            try {
                User serverUser = new Gson().fromJson(stateJson, User.class);
                if (serverUser != null) {
                    serverUser.setPassword(PvZ2.APproject.controllers.menus.SignupMenuController.hashPassword(loginPassword));
                    serverUser.setStayLoggedIn(currentUser != null && currentUser.isStayLoggedIn());
                    setCurrentUser(serverUser);
                    return;
                }
            } catch (Exception e) {
                System.err.println("Could not restore server game state: " + e.getMessage());
            }
        }
        if (currentUser == null) {
            Gender gender = "female".equalsIgnoreCase(data.get("gender")) ? Gender.female : Gender.male;
            setCurrentUser(new User(data.get("username"), SignupMenuController.hashPassword(loginPassword),
                data.get("nickname"), data.get("email"), gender));
        }
        if (data.containsKey("username")) currentUser.setUsername(data.get("username"));
        if (data.containsKey("nickname")) currentUser.setNickname(data.get("nickname"));
        if (data.containsKey("email")) currentUser.setEmail(data.get("email"));
        if (data.containsKey("coins")) currentUser.setCoinsCount(parseInt(data.get("coins"), currentUser.getCoinsCount()));
        if (data.containsKey("gems")) currentUser.setGemsCount(parseInt(data.get("gems"), currentUser.getGemsCount()));
        if (data.containsKey("minigamesWon")) currentUser.setMinigamesWonCount(parseInt(data.get("minigamesWon"), currentUser.getMinigamesWonCount()));
        if (data.containsKey("highestPoint")) currentUser.setHighestPointAchieved(parseInt(data.get("highestPoint"), currentUser.getHighestPointAchieved()));
    }

    private static int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value); } catch (Exception e) { return fallback; }
    }

    public static void setResetToken(String token) { resetToken = token; }
    public static String getResetToken() { return resetToken; }
    public static void setResetUsername(String username) { resetUsername = username; }
    public static String getResetUsername() { return resetUsername; }

    /** Export any user for legacy-account migration without changing currentUser. */
    public static String exportCurrentUserStateFor(User user) {
        if (user == null) return "";
        Gson gson = new Gson();
        JsonObject root = gson.toJsonTree(user).getAsJsonObject();
        root.remove("password");
        root.remove("stayLoggedIn");
        return gson.toJson(root);
    }

    /// Phase 3 implementation done ///

    public static void initialize() {
        allSeasons = new ArrayList<>(SeasonController.getInstance().getActiveSeasons());
        defaultSeasonUnlocks.clear();
        defaultLevelUnlocks.clear();
        for (Season season : allSeasons) {
            defaultSeasonUnlocks.put(season, season.isUnlocked());
            if (season.getLevels() != null) {
                for (LevelData level : season.getLevels()) defaultLevelUnlocks.put(level, level.isUnlocked());
            }
        }
        for (User user : users) user.ensureDefaults();
        if (currentUser != null) setCurrentUser(currentUser);
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
        resetSeasonProgress();
        App.currentUser = currentUser;
        if (currentUser != null) {
            currentUser.ensureDefaults();
            currentUser.restoreProgress();
        }
    }

    private static void resetSeasonProgress() {
        for (Season season : allSeasons) {
            season.setUnlocked(defaultSeasonUnlocks.getOrDefault(season, season.getData().isUnlocked()));
            if (season.getLevels() != null) {
                for (LevelData level : season.getLevels()) {
                    level.setUnlocked(defaultLevelUnlocks.getOrDefault(level, level.isUnlocked()));
                }
            }
        }
    }

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
        if (username == null) return false;
        for (User user : users) {
            if (user.getUsername() != null && user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
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
            if (season == null) continue;
            if ((season.getType() != null && (season.getType().getName().equalsIgnoreCase(seasonName) ||
                season.getType().name().equalsIgnoreCase(seasonName.replace(" ", "_")))) ||
                (season.getName() != null && season.getName().equalsIgnoreCase(seasonName))) {
                return season;
            }
        }
        return null;
    }

    public static SeasonData getSeasonData(String seasonName) {
        seasonName = seasonName.trim();
        for (SeasonData season : SeasonRepository.getInstance().getAllSeasons()) {
            if (season == null) continue;
            if ((season.getName().equalsIgnoreCase(seasonName)) ||
                (season.getName() != null && season.getName().equalsIgnoreCase(seasonName))) {
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
