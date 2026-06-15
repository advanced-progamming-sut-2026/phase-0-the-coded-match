package models;

import enums.Gender;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String username;
    private String password;
    private String nickname;
    private String email;
    private Gender gender;
    private int gamesPlayedCount;
    private int coinsCount;
    private int gemsCount;
    private int chaptersCount;
    private int meowPoints;
    private LevelData lastLevel;
    private Season lastSeason;
    private int minigamesWonCount;
    private int dailyQuestsCount;
    private int questsCount;
    private int highestPointAchieved;
    private boolean stayLoggedIn;
    private Collection collection;
    private GreenHouse greenHouse;
    private ArrayList<Level> unlockedLevels = new ArrayList<>();

    public User(String username, String password, String nickname, String email, Gender gender) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
    }

    public void addGamesPlayed() {}

    public void addCoins() {}

    public void addGems() {}

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
}
