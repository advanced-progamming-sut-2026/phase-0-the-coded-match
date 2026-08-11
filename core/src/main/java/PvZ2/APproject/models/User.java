package PvZ2.APproject.models;

import PvZ2.APproject.enums.Gender;
import PvZ2.APproject.enums.QuestRelated.QuestCategory;
import PvZ2.APproject.enums.SecurityQuestions;
import PvZ2.APproject.models.greenhouse.GreenHouse;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;
import PvZ2.APproject.models.seasons.Season;

import java.util.ArrayList;
import java.util.*;

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
    private transient LevelData lastLevel;
    private int lastLevelNumber;
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
    private int plantFoodBoughtCount = 0;
    private String lastDailyQuestRefreshDate;

    public User(String username, String password, String nickname, String email, Gender gender) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.questions = new HashMap<>();
        this.difficultyLevel = 3;
        this.collection = new Collection();
        this.shop = new Shop();
        this.greenHouse = new GreenHouse();
        this.questsModel = new QuestsModel();
        this.personalNews = new NewsManager();
    }

    public void addGamesPlayed() { gamesPlayedCount++; }

    public void addCoins(int amount) {
        this.coinsCount = coinsCount + amount;
    }

    public void addGems(int amount) {
        this.gemsCount = gemsCount + amount;
    }

    public void addChapters() { levelsCount++; }

    public void addMeowPoints() { meowPoints++; }
    public void addMeowPoints(int amount) { meowPoints += Math.max(0, amount); }

    public void addMinigamesWon() { minigamesWonCount++; }

    public void addDailyQuests() {}

    public void ensureDefaults() {
        if (questions == null) questions = new HashMap<>();
        if (collection == null) collection = new Collection();
        if (collection.getAvailablePlantsIds().isEmpty()) for (String name : List.of("Peashooter", "Sunflower", "Wall-nut")) { PlantData p = PlantRepository.getInstance().findByName(name); if (p != null) collection.unlockPlant(p.getId()); }
        if (greenHouse == null) greenHouse = new GreenHouse();
        if (shop == null) shop = new Shop();
        if (unlockedLevels == null) unlockedLevels = new ArrayList<>();
        if (personalNews == null) personalNews = new NewsManager();
        if (seedPackets == null) seedPackets = new HashMap<>();
        if (questsModel == null) questsModel = new QuestsModel();
        if (difficultyLevel < 1 || difficultyLevel > 5) difficultyLevel = 3;
        restoreProgress();
    }

    private void restoreProgress() {
        if (lastSeasonId <= 0) return;
        for (Season season : App.getAllSeasons()) {
            int seasonId = season.getData().getId();
            if (seasonId < lastSeasonId) {
                season.setUnlocked(true);
                for (LevelData level : season.getLevels()) level.setUnlocked(true);
            } else if (seasonId == lastSeasonId) {
                season.setUnlocked(true);
                lastSeason = season;
                int maxLevel = 0;
                for (LevelData level : season.getLevels()) {
                    maxLevel = Math.max(maxLevel, level.getLevelNumber());
                    if (level.getLevelNumber() <= Math.max(1, lastLevelNumber + 1)) level.setUnlocked(true);
                    if (level.getLevelNumber() == lastLevelNumber) lastLevel = level;
                }
                if (lastLevelNumber >= maxLevel) {
                    for (Season nextSeason : App.getAllSeasons()) {
                        if (nextSeason.getData().getId() == lastSeasonId + 1) {
                            nextSeason.setUnlocked(true);
                            if (!nextSeason.getLevels().isEmpty()) nextSeason.getLevels().get(0).setUnlocked(true);
                            break;
                        }
                    }
                }
            }
        }
    }
    public void recordLevelVictory(Season season, LevelData level, int score) {
        this.lastSeason = season;
        if (season != null && season.getData() != null) {
            this.lastSeasonId = season.getData().getId();
        }
        this.lastLevel = level;
        if (level != null) this.lastLevelNumber = level.getLevelNumber();
        if (score > this.highestPointAchieved) {
            this.highestPointAchieved = score;
        }
    }

    public int getCompletedQuestsCount() {
        ensureDefaults();
        int count = 0;
        for (Quest quest : questsModel.getAvailableQuests()) {
            if (quest.isCompleted) {
                count++;
            }
        }
        return count;
    }

    public int getCompletedDailyQuestsCount() {
        ensureDefaults();
        int count = 0;
        for (Quest quest : questsModel.getAvailableQuests()) {
            if (quest.isCompleted && quest.getQuestData().getCategory() == QuestCategory.DAILY) {
                count++;
            }
        }
        return count;
    }

    public void addCollection() {}

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
        return lastSeason;
    }

    public void setLastSeason(Season lastSeason) {
        this.lastSeason = lastSeason;
        if (lastSeason != null && lastSeason.getData() != null) this.lastSeasonId = lastSeason.getData().getId();
    }

    public int getMinigamesWonCount() {
        return minigamesWonCount;
    }

    public void setMinigamesWonCount(int minigamesWonCount) {
        this.minigamesWonCount = minigamesWonCount;
    }

    public int getHighestPointAchieved() {
        return highestPointAchieved;
    }

    public void setHighestPointAchieved(int highestPointAchieved) {
        this.highestPointAchieved = highestPointAchieved;
    }

    public void addQuestion(SecurityQuestions question, String answer){
        ensureDefaults();
        this.questions.put(question, answer);
    }

    public String getPassword(){
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

    public void setStayLoggedIn(boolean stayLoggedIn){ this.stayLoggedIn = stayLoggedIn; }

    public boolean isStayLoggedIn() { return stayLoggedIn; }

    public Map<SecurityQuestions, String> getQuestions(){
        ensureDefaults();
        return questions;
    }

    public NewsManager getPersonalNews(){
        ensureDefaults();
        return personalNews;
    }

    public int getCoinsCount() {
        return coinsCount;
    }

    public void setCoinsCount(int coinsCount) {
        this.coinsCount = coinsCount;
    }

    public int getGemsCount() {
        return gemsCount;
    }

    public void setGemsCount(int gemsCount) {
        this.gemsCount = gemsCount;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
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
        ensureDefaults();
        return seedPackets.getOrDefault(plantName.toLowerCase(), 0);
    }

    public int getAllSeedPacketsCount() {
        int sum = 0;
        for (int amount : seedPackets.values()) {
            sum += amount;
        }
        return sum;
    }

    public void addSeedPackets(String plantName, int count) {
        String key = plantName.toLowerCase();
        seedPackets.put(key, getSeedPacketCount(key) + count);
    }

    public boolean spendSeedPackets(String plantName, int count) {
        String key = plantName.toLowerCase();
        int current = getSeedPacketCount(key);
        if (current < count) {
            return false;
        }
        seedPackets.put(key, current - count);
        return true;
    }

    public Collection getCollection(){
        ensureDefaults();
        return collection;
    }

    public QuestsModel getQuestsModel() {
        ensureDefaults();
        return questsModel;
    }

    public void setQuestsModel(QuestsModel questsModel) {
        this.questsModel = questsModel;
    }

    public int getPlantFoodBoughtCount() {
        return plantFoodBoughtCount;
    }

    public void setPlantFoodBoughtCount(int plantFoodBoughtCount) {
        this.plantFoodBoughtCount = plantFoodBoughtCount;
    }

    public String getLastDailyQuestRefreshDate() {
        return lastDailyQuestRefreshDate;
    }

    public void setLastDailyQuestRefreshDate(String lastDailyQuestRefreshDate) {
        this.lastDailyQuestRefreshDate = lastDailyQuestRefreshDate;
    }

    public boolean isVictroy() {
        return isVictroy;
    }
    public void setVictroy(boolean isVictroy){
        this.isVictroy = isVictroy;
    }

    public GreenHouse getGreenHouse(){
        ensureDefaults();
        return greenHouse;
    }

    public Shop getShop() {
        ensureDefaults();
        return shop;
    }
}
