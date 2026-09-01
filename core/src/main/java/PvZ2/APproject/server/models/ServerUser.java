package PvZ2.APproject.server.models;

import com.badlogic.gdx.utils.Json;

import java.io.Serializable;

public class ServerUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String username;
    private String passwordHash;
    private String nickname;
    private String email;
    private String gender;
    private int coins;
    private int gems;
    private int minigamesWon;
    private int highestPoint;
    private int gamesPlayed;

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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String v) {
        passwordHash = v;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public int getCoins() {
        return coins;
    }

    public int getGems() {
        return gems;
    }

    public int getMinigamesWon() {
        return minigamesWon;
    }

    public int getHighestPoint() {
        return highestPoint;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void addMinigameWin() {
        minigamesWon++;
    }

    public void incrementGamesPlayed() {
        gamesPlayed++;
    }

    public void setHighestPoint(int p) {
        highestPoint = Math.max(highestPoint, p);
    }
}

