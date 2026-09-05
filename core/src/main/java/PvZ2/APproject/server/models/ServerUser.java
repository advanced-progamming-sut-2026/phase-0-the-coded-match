package PvZ2.APproject.server.models;

import com.badlogic.gdx.utils.Json;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ServerUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String passwordHash;
    private String nickname;
    private String email;
    private String gender;
    private int coins;
    private int gems;
    private int minigamesWon;
    private int highestPoint;
    private int gamesPlayed;

    private int levelsCount;
    private int meowPoints;
    private int lastSeasonId;
    private int lastLevelNumber;
    private int completedQuests;
    private int completedDailyQuests;

    private String gameStateJson = "";

    private Map<String, String> securityQuestions = new HashMap<>();

    public ServerUser(String username, String passwordHash, String nickname, String email, String gender) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String v) {
        passwordHash = v;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) { this.email = email; }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) { this.gender = gender; }

    public int getCoins() {
        return coins;
    }
    public void setCoins(int coins) { this.coins = Math.max(0, coins); }

    public int getGems() {
        return gems;
    }
    public void setGems(int gems) { this.gems = Math.max(0, gems); }

    public int getMinigamesWon() {
        return minigamesWon;
    }
    public void setMinigamesWon(int value) { minigamesWon = Math.max(0, value); }
    public void addMinigameWin() {
        minigamesWon++;
    }

    public int getHighestPoint() {
        return highestPoint;
    }
    public void setHighestPoint(int value) { highestPoint = Math.max(highestPoint, Math.max(0, value)); }

    public int getGamesPlayed() {
        return gamesPlayed;
    }
    public void setGamesPlayed(int value) { gamesPlayed = Math.max(0, value); }
    public void incrementGamesPlayed() {
        gamesPlayed++;
    }

    public int getLevelsCount() { return levelsCount; }
    public void setLevelsCount(int value) { levelsCount = Math.max(0, value); }

    public int getMeowPoints() { return meowPoints; }
    public void setMeowPoints(int value) { meowPoints = Math.max(0, value); }

    public int getLastSeasonId() { return lastSeasonId; }
    public void setLastSeasonId(int value) { lastSeasonId = Math.max(0, value); }

    public int getLastLevelNumber() { return lastLevelNumber; }
    public void setLastLevelNumber(int value) { lastLevelNumber = Math.max(0, value); }

    public int getCompletedQuests() { return completedQuests; }
    public void setCompletedQuests(int value) { completedQuests = Math.max(0, value); }

    public int getCompletedDailyQuests() { return completedDailyQuests; }
    public void setCompletedDailyQuests(int value) { completedDailyQuests = Math.max(0, value); }

    public String getGameStateJson() { return gameStateJson == null ? "" : gameStateJson; }
    public void setGameStateJson(String gameStateJson) { this.gameStateJson = gameStateJson == null ?
        "" : gameStateJson; }

    public Map<String, String> getSecurityQuestions() {
        if (securityQuestions == null) securityQuestions = new HashMap<>();
        return securityQuestions;
    }
    public void setSecurityQuestions(Map<String, String> questions) {
        securityQuestions = questions == null ? new HashMap<>() : new HashMap<>(questions);
    }
}

