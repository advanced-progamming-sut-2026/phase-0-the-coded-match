package controllers;

import models.Zombie;
import models.ZombieRepository;

import java.util.ArrayList;
import java.util.List;

public class ZombieController {

    private final ZombieRepository zombieRepository;
    private final List<Zombie> activeZombies;

    public ZombieController() {
        this.zombieRepository = new ZombieRepository("assets/Data/zombies.json");
        this.activeZombies = new ArrayList<>();
    }

    public Zombie spawnZombie(String alias, double x, int y) {
        //TODO
        return null;
    }

    public void moveZombies() {
        //TODO
    }

    public void attackPlants() {
        // TODO
    }

    public void removeDeadZombies() {
       //TODO
    }

    public void showZombieInfo() {
       //TODO
    }

    public List<Zombie> getActiveZombies() {
        return activeZombies;
    }
}