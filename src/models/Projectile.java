package models;

import controllers.GameManagerController;
import models.plants.Plant;
import models.zombies.Zombie;

public class Projectile {
    private double xCoordinate;
    private double yCoordinate;
    private double speed;
    private int damage;
    private boolean isMovingLeft;
    private boolean isDestroyed;

    public Projectile(double xCoordinate, double yCoordinate, double speed, int damage, boolean isMovingLeft, boolean isDestroyed) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.speed = speed;
        this.damage = damage;
        this.isMovingLeft = isMovingLeft;
        this.isDestroyed = isDestroyed;
    }

    public void move() {}
    public boolean checkPlantCollision(Plant plant) { return false; }
    public boolean checkZombieCollision(Zombie zombie) { return false; }
    public void destroy() {
        isDestroyed = true;
        GameManagerController.getCurrentLevel().getActiveProjectiles().remove(this);
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public double getSpeed() {
        return speed;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isMovingLeft() {
        return isMovingLeft;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }
}
