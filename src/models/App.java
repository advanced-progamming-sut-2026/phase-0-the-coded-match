package models;

import controllers.ZombieWaveManager;
import enums.Menu;

import java.util.ArrayList;
import java.util.List;

public class App {
    private Menu currentMenu;
    private ArrayList<User> users;
    private int difficulty;
    private Chapter currentChapter;
    private User currentUser;

    private Season currentSeason;
    private List<Lawnmower> lawnmowers;
    private List<Plant> plants;
    private List<Zombie> zombies;
    private List<Sun> suns;
    private int currentTick;
    private int waveDifficulty;
    private ZombieWaveManager zombieWave;
    private List<Projectile> activeProjectiles;

    public static boolean doesUsernameExists(String username) {

    }
}
