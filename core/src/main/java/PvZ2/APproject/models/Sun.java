package PvZ2.APproject.models;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.controllers.QuestController;
import PvZ2.APproject.enums.SunType;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

public class Sun implements Update {

    protected float x;
    protected float y;
    protected SunType type;
    protected int value;
    protected float timeToReachGround;
    protected boolean isFalling;
    protected boolean hasFallen;

    public Sun(float x, float y, int value, float timeToReachGround, boolean isFalling, SunType type) { //if plant is producing: 0, false
        this.x = x;
        this.y = y;
        this.value = value;
        this.timeToReachGround = timeToReachGround;
        this.isFalling = isFalling;
        this.hasFallen = !isFalling;
        this.type = type;
    }

    public void collect() {
        Level currentLevel = GameManagerController.getInstance().getCurrentLevel();
        if (isFalling && type == SunType.RADIOACTIVE) {
            explode();
            currentLevel.getActiveSuns().remove(this);
            return;
        } else if (type == SunType.RADIOACTIVE) {
            this.type = SunType.NORMAL;
            this.value = SunType.NORMAL.getValue();
        }
        currentLevel.setCollectedSunsAmount(currentLevel.getCollectedSunsAmount() + value);
        QuestController.notifySunCollected(value);
        currentLevel.getActiveSuns().remove(this);
    }

    public void explode() {
        for (Zombie zombie : GameManagerController.getInstance().getCurrentLevel().getActiveZombies()) {
            if (Math.abs(zombie.getX() - x) <= 2 && Math.abs(zombie.getY() - y) <= 2) {
                zombie.takeDamage(150, null);
            }
        }
        for (Plant plant : GameManagerController.getInstance().getCurrentLevel().getActivePlants()) {
            if (Math.abs(plant.getX() - x) <= 1 && Math.abs(plant.getY() - y) <= 1) {
                plant.takeDamage(80);
            }
        }
    }

    @Override
    public void update(float delta) {
        if (isFalling) {
            timeToReachGround -= delta;
            if (timeToReachGround <= 0) {
                isFalling = false;
                hasFallen = true;
                if (type == SunType.RADIOACTIVE) {
                    type = SunType.NORMAL;
                    value = SunType.NORMAL.getValue();
                }
            }
        }
    }

    public float getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public float getY() {
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

    public float getTimeToReachGround() {
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
