package models.GameMapRelated;

import controllers.GameManagerController;
import enums.PlantTag;
import enums.TileType;
import models.Update;
import models.plants.Plant;
import models.zombies.Zombie;

import java.util.List;

public class Tile implements Update {

    private int row;
    private int column;
    private TileType type;
    private int currentHp;
    private Plant plant;
    private List<Zombie> zombies;
    private boolean isGettingDamaged = false;

    public Tile(int row, int column, TileType type) {
        this.row = row;
        this.column = column;
        this.type = type;
        this.currentHp = type.getMaxHp();
    }

    public void takeDamage(int damage) {
        if (currentHp > 0) {
            currentHp -= damage;
            if (currentHp <= 0) {
                currentHp = 0;
                this.type = TileType.NORMAL;
            }
        }
    }

    public void startTakingDamage() {
        if (type == TileType.ICE && firePlantExists()) {
            currentHp -= 60;
            this.isGettingDamaged = true;
        }
    }

    public void stopTakingDamage() {
        if (type == TileType.ICE && currentHp <= 0) {
            currentHp = 0;
            this.isGettingDamaged = false;
        }
    }

    public boolean firePlantExists() {
        for (Plant p : GameManagerController.getCurrentLevel().getActivePlants()) {
            if (p.hasThisTag(PlantTag.FIRE) && (p.getX() - column <= 1) && (p.getY() - row <= 1)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return plant == null;
    }

    public static void removePlant() {

    }

    @Override
    public void update() {
        startTakingDamage();
        stopTakingDamage();
    }
}
