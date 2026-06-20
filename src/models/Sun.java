package models;

import controllers.GameManagerController;
import enums.SunType;
import models.plants.Plant;
import models.zombies.Zombie;

public class Sun implements Update {

    protected int x;
    protected int y;
    protected SunType type;
    protected int value;
    protected int timeToReachGround;
    protected boolean isFalling;
    protected boolean hasFallen;

    public Sun(int x, int y, int value, int timeToReachGround, boolean isFalling, SunType type) { //TODO: if plant is producing: 0, false

        this.x = x;
        this.y = y;
        this.value = value;
        this.timeToReachGround = timeToReachGround;
        this.isFalling = isFalling;
        this.hasFallen = isFalling;
        this.type = type;
    }

    public void collect() {
        Level currentLevel = GameManagerController.getCurrentLevel();
        if (isFalling && type == SunType.RADIOACTIVE) {
            explode();
            currentLevel.getActiveSuns().remove(this);
            return;
        } else if (type == SunType.RADIOACTIVE) {
            this.type = SunType.NORMAL;
        }
        currentLevel.setCollectedSunsAmount(currentLevel.getCollectedSunsAmount() + value);
        currentLevel.getActiveSuns().remove(this);
    }

    public void explode() {
        for (Zombie zombie : GameManagerController.getCurrentLevel().getActiveZombies()) {
            if ((zombie.getX() - this.x <= 2) && (zombie.getY() - this.y <= 2)) {
                zombie.setCurrentHp(zombie.getCurrentHp() - 150);
                if (zombie.isDead()) {
                    //TODO: call the method that should be called after a zombie dies
                }
            }
        }
        for (Plant plant : GameManagerController.getCurrentLevel().getActivePlants()) {
            if ((plant.getX() - this.x <= 1) && (plant.getY() - this.y <= 1)) {
                plant.setCurrentHp(plant.getCurrentHp() - 80);
                if (plant.isDead()) {
                    //TODO: call the method that should be called after a plant dies
                }
            }
        }
    }

    @Override
    public void update() {
        if (isFalling) {
            timeToReachGround -= 1;
            if (timeToReachGround == 0) {
                isFalling = false;
            }
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getTimeToReachGround() {
        return timeToReachGround;
    }

    public void setTimeToReachGround(int timeToReachGround) {
        this.timeToReachGround = timeToReachGround;
    }

    public boolean isFalling() {
        return isFalling;
    }

    public void setFalling(boolean falling) {
        isFalling = falling;
    }

    public boolean hasFallen() {
        return hasFallen;
    }

    public void setFallen(boolean hasFallen) {
        this.hasFallen = hasFallen;
    }

    public SunType getType() {
        return type;
    }

    public void setType(SunType type) {
        this.type = type;
    }
}
