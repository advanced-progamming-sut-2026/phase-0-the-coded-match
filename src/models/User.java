package models;

import enums.Gender;
import enums.SecurityQuestions;
import models.greenhouse.GreenHouse;
import models.seasons.Season;

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
    private int chaptersCount;
    private int meowPoints;
    private LevelData lastLevel;
    private Season lastSeason;
    private int difficultyLevel;
    private int minigamesWonCount;
    private int dailyQuestsCount;
    private int questsCount;
    private int highestPointAchieved;
    private boolean stayLoggedIn;
    private Collection collection;
    private GreenHouse greenHouse;
    private ArrayList<Level> unlockedLevels = new ArrayList<>();
    private NewsManager personalNews;
    private Map<String, Integer> seedPackets = new HashMap<>();
    private QuestsModel questsModel;

    public User(String username, String password, String nickname, String email, Gender gender) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.questions = new HashMap<>();
        this.difficultyLevel = 3;
        this.collection = new Collection();
    }

    public void addGamesPlayed() {}

    public void addCoins(int amount) {
        this.coinsCount = coinsCount + amount;
    }

    public void addGems(int amount) {
        this.gemsCount = gemsCount + amount;
    }

    public void addChapters() {}

    public void addMeowPoints() {}

    public void addMinigamesWon() {}

    public void addDailyQuests() {}

    public void addQuestsCount() {}

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
    }

    public void addQuestion(SecurityQuestions question, String answer){
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

    public void setStayLoggedIn(boolean stayLoggedIn){
        this.stayLoggedIn = stayLoggedIn;
    }

    public Map<SecurityQuestions, String> getQuestions(){
        return questions;
    }

    public NewsManager getPersonalNews(){
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

    public int getChaptersCount() {
        return chaptersCount;
    }

    public int getMeowPoints() {
        return meowPoints;
    }

    public int getSeedPacketCount(String plantName) {
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
        return collection;
    }

    public QuestsModel getQuestsModel() {
        return questsModel;
    }

    public void setQuestsModel(QuestsModel questsModel) {
        this.questsModel = questsModel;
    }
}
