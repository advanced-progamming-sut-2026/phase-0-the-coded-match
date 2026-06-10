package models;

import Model.Collection;

import java.util.List;

public class User {

    private String username;
    private String password;
    private String nickname;
    private String email;
    private String gender;
    private int gamesPlayedCount;
    private int coinsCount;
    private int gemsCount;
    private int chaptersCount;
    private int meowPoints;
    private int lastChapter;
    private int lastWorld;
    private int minigamesWonCount;
    private int dailyQuestsCount;
    private int questsCount;
    private int highestPointAchieved;
    private boolean stayLoggedIn;
    private List<Collection> collections;

    public User(String username, String password, String nickname, String email, String gender) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
    }
}
