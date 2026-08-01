package models;

import controllers.QuestController;
import enums.Gender;
import enums.QuestRelated.QuestCategory;
import enums.SecurityQuestions;
import models.greenhouse.GreenHouse;
import models.seasons.Season;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private String password;
    private String nickname;
    private String email;
    private Gender gender;
    private Map<SecurityQuestions, String> questions;
    private int gamesPlayedCount;
    private int coinsCount;
    private int gemsCount;
    private int levelsCount;
    private int meowPoints;
    private LevelData lastLevel;
    private boolean isVictroy;
    private transient Season lastSeason;
    private int lastSeasonId;
    private int difficultyLevel;
    private int minigamesWonCount;
    private int highestPointAchieved;
    private boolean stayLoggedIn;
    private Collection collection;
    private GreenHouse greenHouse;
    private Shop shop;
    private transient ArrayList<Level> unlockedLevels = new ArrayList<>();
    private NewsManager personalNews;
    private Map<String, Integer> seedPackets = new HashMap<>();
    private QuestsModel questsModel;
    private int plantFoodBoughtCount;
    private String lastDailyQuestRefreshDate;
    private int questWinStreak;
    private int completedDailyQuestsTotal;
    private int completedNonDailyQuestsTotal;
    private transient QuestRuntimeState questRuntimeState;

    public User(String username, String password, String nickname, String email, Gender gender) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        questions = new HashMap<>();
        difficultyLevel = 3;
        collection = new Collection();
        unlockStarterPlants();
        shop = new Shop();
        greenHouse = new GreenHouse();
        questsModel = new QuestsModel();
    }

    private void ensureInitialized() {
        if (questions == null) {
            questions = new HashMap<>();
        }
        if (collection == null) {
            collection = new Collection();
        }
        if (collection.getAvailablePlantsIds().isEmpty()) {
            unlockStarterPlants();
        }
        if (shop == null) {
            shop = new Shop();
        }
        if (greenHouse == null) {
            greenHouse = new GreenHouse();
        }
        if (questsModel == null) {
            questsModel = new QuestsModel();
        }
        if (seedPackets == null) {
            seedPackets = new HashMap<>();
        }
        if (unlockedLevels == null) {
            unlockedLevels = new ArrayList<>();
        }
        if (questRuntimeState == null) {
            questRuntimeState = new QuestRuntimeState();
        }
        if (difficultyLevel < 1 || difficultyLevel > 5) {
            difficultyLevel = 3;
        }
    }

    private void unlockStarterPlants() {
        if (collection == null) {
            collection = new Collection();
        }
        collection.unlockPlant("peashooter");
        collection.unlockPlant("sunflower");
        collection.unlockPlant("wall_nut");
    }

    public void addGamesPlayed() {
        gamesPlayedCount++;
    }

    public void addCoins(int amount) {
        coinsCount = Math.max(0, coinsCount + amount);
    }

    public void addGems(int amount) {
        gemsCount = Math.max(0, gemsCount + amount);
    }

    public void addChapters() {
        levelsCount++;
    }

    public void addLevelCompleted() {
        levelsCount++;
    }

    public void addMeowPoints() {
        addMeowPoints(1);
    }

    public void addMeowPoints(int amount) {
        meowPoints = Math.max(0, meowPoints + amount);
        highestPointAchieved = Math.max(highestPointAchieved, meowPoints);
    }

    public void resetMeowPoints() {
        meowPoints = 0;
    }

    public void addMinigamesWon() {
        minigamesWonCount++;
    }

    public void addDailyQuests() {
        QuestController.refreshDailyQuests();
    }

    public int getCompletedQuestsCount() {
        return getCompletedDailyQuestsCount() + getCompletedNonDailyQuestsCount();
    }

    public int getCompletedDailyQuestsCount() {
        ensureInitialized();
        int currentCompleted = 0;
        for (Quest quest : questsModel.getAvailableQuests()) {
            if (quest.isCompleted() && quest.getCategory() == QuestCategory.DAILY) {
                currentCompleted++;
            }
        }
        return Math.max(completedDailyQuestsTotal, currentCompleted);
    }

    public int getCompletedNonDailyQuestsCount() {
        ensureInitialized();
        int currentCompleted = 0;
        for (Quest quest : questsModel.getAvailableQuests()) {
            if (quest.isCompleted() && quest.getCategory() != QuestCategory.DAILY) {
                currentCompleted++;
            }
        }
        return Math.max(completedNonDailyQuestsTotal, currentCompleted);
    }

    public void recordQuestCompletion(QuestCategory category) {
        if (category == QuestCategory.DAILY) {
            completedDailyQuestsTotal++;
        } else {
            completedNonDailyQuestsTotal++;
        }
    }

    public void addCollection() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LevelData getLastLevel() {
        return lastLevel;
    }

    public void setLastLevel(LevelData lastLevel) {
        this.lastLevel = lastLevel;
    }

    public Season getLastSeason() {
        if (lastSeason == null && lastSeasonId > 0) {
            for (Season season : App.getAllSeasons()) {
                if (season.getData().getId() == lastSeasonId) {
                    lastSeason = season;
                    break;
                }
            }
        }
        return lastSeason;
    }

    public void setLastSeason(Season lastSeason) {
        this.lastSeason = lastSeason;
        lastSeasonId = lastSeason == null ? 0 : lastSeason.getData().getId();
    }

    public int getLastSeasonId() {
        return lastSeasonId;
    }

    public int getMinigamesWonCount() {
        return minigamesWonCount;
    }

    public void setMinigamesWonCount(int minigamesWonCount) {
        this.minigamesWonCount = Math.max(0, minigamesWonCount);
    }

    public int getHighestPointAchieved() {
        return highestPointAchieved;
    }

    public void setHighestPointAchieved(int highestPointAchieved) {
        this.highestPointAchieved = Math.max(this.highestPointAchieved, highestPointAchieved);
    }

    public void addQuestion(SecurityQuestions question, String answer) {
        ensureInitialized();
        questions.put(question, answer);
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStayLoggedIn(boolean stayLoggedIn) {
        this.stayLoggedIn = stayLoggedIn;
    }

    public Map<SecurityQuestions, String> getQuestions() {
        ensureInitialized();
        return questions;
    }

    public NewsManager getPersonalNews() {
        return personalNews;
    }

    public int getCoinsCount() {
        return coinsCount;
    }

    public void setCoinsCount(int coinsCount) {
        this.coinsCount = Math.max(0, coinsCount);
    }

    public int getGemsCount() {
        return gemsCount;
    }

    public void setGemsCount(int gemsCount) {
        this.gemsCount = Math.max(0, gemsCount);
    }

    public int getDifficultyLevel() {
        ensureInitialized();
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = Math.max(1, Math.min(5, difficultyLevel));
    }

    public int getGamesPlayedCount() {
        return gamesPlayedCount;
    }

    public int getLevelsCount() {
        return levelsCount;
    }

    public int getMeowPoints() {
        return meowPoints;
    }

    public int getSeedPacketCount(String plantName) {
        ensureInitialized();
        return seedPackets.getOrDefault(plantName.toLowerCase(), 0);
    }

    public int getAllSeedPacketsCount() {
        ensureInitialized();
        int sum = 0;
        for (int amount : seedPackets.values()) {
            sum += amount;
        }
        return sum;
    }

    public void addSeedPackets(String plantName, int count) {
        ensureInitialized();
        String key = plantName.toLowerCase();
        seedPackets.put(key, getSeedPacketCount(key) + count);
    }

    public boolean spendSeedPackets(String plantName, int count) {
        ensureInitialized();
        String key = plantName.toLowerCase();
        int current = getSeedPacketCount(key);
        if (current < count) {
            return false;
        }
        seedPackets.put(key, current - count);
        return true;
    }

    public Collection getCollection() {
        ensureInitialized();
        return collection;
    }

    public QuestsModel getQuestsModel() {
        ensureInitialized();
        return questsModel;
    }

    public void setQuestsModel(QuestsModel questsModel) {
        this.questsModel = questsModel;
    }


    public String getLastDailyQuestRefreshDate() {
        return lastDailyQuestRefreshDate;
    }

    public void setLastDailyQuestRefreshDate(String lastDailyQuestRefreshDate) {
        this.lastDailyQuestRefreshDate = lastDailyQuestRefreshDate;
    }

    public int getQuestWinStreak() {
        return questWinStreak;
    }

    public void setQuestWinStreak(int questWinStreak) {
        this.questWinStreak = Math.max(0, questWinStreak);
    }

    public QuestRuntimeState getQuestRuntimeState() {
        ensureInitialized();
        return questRuntimeState;
    }

    public int getPlantFoodBoughtCount() {
        return plantFoodBoughtCount;
    }

    public void setPlantFoodBoughtCount(int plantFoodBoughtCount) {
        this.plantFoodBoughtCount = Math.max(0, plantFoodBoughtCount);
    }

    public boolean isVictroy() {
        return isVictroy;
    }

    public void setVictroy(boolean victroy) {
        isVictroy = victroy;
    }

    public GreenHouse getGreenHouse() {
        ensureInitialized();
        return greenHouse;
    }

    public Shop getShop() {
        ensureInitialized();
        return shop;
    }
}
