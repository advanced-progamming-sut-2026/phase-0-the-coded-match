package models.GameMapRelated;

import controllers.GameManagerController;
import enums.PlantTag;
import enums.TileType;
import models.Update;
import models.plants.Plant;
import models.zombies.Zombie;

import java.util.ArrayList;
import java.util.List;

public class Tile implements Update {
    private int row;
    private int column;
    private int tileWidth; //TODO: choose an optional width?
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
        this.zombies = new ArrayList<>();
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
            this.type = TileType.NORMAL;
        }
    }

    public boolean firePlantExists() {
        for (Plant p : GameManagerController.getCurrentLevel().getActivePlants()) {
            int dx = Math.abs(p.getX() - column);
            int dy = Math.abs(p.getY() - row);
            if (p.hasThisTag(PlantTag.FIRE) && dx <= 1 && dy <= 1) {
                return true;
            }
        }
        return false;
    }

    public boolean isPlantable() {
        return type == TileType.NORMAL || type == TileType.WATER;
    }

    public boolean isEmpty() {
        return plant == null;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Plant getPlant() {
        return plant;
    }

    public void removePlantFromTile() {
        this.plant = null;
    }

    public static void removePlant() {
    }

    @Override
    public void update() {
        startTakingDamage();
        stopTakingDamage();
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public boolean isGettingDamaged() {
        return isGettingDamaged;
    }

    public List<Zombie> getZombies() {
        return zombies;
    }
}