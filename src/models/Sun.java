package models;

import controllers.GameManagerController;
import controllers.QuestController;
import enums.SunType;
import models.plants.Plant;
import models.zombies.Zombie;

import java.util.ArrayList;

public class Sun implements Update {
    protected int x;
    protected int y;
    protected SunType type;
    protected int value;
    protected int timeToReachGround;
    protected boolean isFalling;
    protected boolean hasFallen;
    private transient Plant sourcePlant;

    public Sun(int x, int y, int value, int timeToReachGround, boolean isFalling, SunType type) {
        this(x, y, value, timeToReachGround, isFalling, type, null);
    }

    public Sun(int x, int y, int value, int timeToReachGround, boolean isFalling, SunType type, Plant sourcePlant) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.timeToReachGround = Math.max(0, timeToReachGround);
        this.isFalling = isFalling;
        this.hasFallen = !isFalling;
        this.type = type == null ? SunType.NORMAL : type;
        this.sourcePlant = sourcePlant;
    }

    public void collect() {
        Level currentLevel = GameManagerController.getInstance().getCurrentLevel();
        if (currentLevel == null) {
            return;
        }
        if (isFalling && type == SunType.RADIOACTIVE) {
            explode();
            currentLevel.getActiveSuns().remove(this);
            return;
        }
        currentLevel.setCollectedSunsAmount(currentLevel.getCollectedSunsAmount() + value);
        if (QuestController.isReady()) {
            QuestController.notifySunCollected(value);
        }
        if (sourcePlant != null) {
            sourcePlant.setSunCollected(true);
        }
        currentLevel.getActiveSuns().remove(this);
    }

    public void explode() {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return;
        }
        for (Zombie zombie : new ArrayList<>(level.getActiveZombies())) {
            if (Math.abs(zombie.getX() - x) <= 2 && Math.abs(zombie.getY() - y) <= 2) {
                zombie.takeDamage(150, null);
            }
        }
        for (Plant plant : new ArrayList<>(level.getActivePlants())) {
            if (Math.abs(plant.getX() - x) <= 1 && Math.abs(plant.getY() - y) <= 1) {
                plant.takeDamage(80);
            }
        }
    }

    @Override
    public void update() {
        if (!isFalling) {
            return;
        }
        timeToReachGround = Math.max(0, timeToReachGround - 1);
        if (timeToReachGround == 0) {
            isFalling = false;
            hasFallen = true;
            if (type == SunType.RADIOACTIVE) {
                type = SunType.NORMAL;
                value = SunType.NORMAL.getValue();
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
        this.timeToReachGround = Math.max(0, timeToReachGround);
    }

    public boolean isFalling() {
        return isFalling;
    }

    public void setFalling(boolean falling) {
        isFalling = falling;
        if (falling) {
            hasFallen = false;
        }
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
        this.type = type == null ? SunType.NORMAL : type;
    }

    public Plant getSourcePlant() {
        return sourcePlant;
    }

    public void setSourcePlant(Plant sourcePlant) {
        this.sourcePlant = sourcePlant;
    }
}
