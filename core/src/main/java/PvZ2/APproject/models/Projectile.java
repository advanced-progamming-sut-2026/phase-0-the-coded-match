package PvZ2.APproject.models;

import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Barrel;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.Zomboss;

public class Projectile {
    private double xCoordinate;
    private double yCoordinate;
    private double speed;
    private int damage;
    private boolean isMovingLeft;
    private boolean isDestroyed;
    private Plant creatorPlantCategory;
    private Zombie targetZombie;
    private Tile targetTile;

    public Projectile(double xCoordinate, double yCoordinate, double speed, int damage, boolean isMovingLeft,
                      boolean isDestroyed, Plant creatorPlantCategory) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.speed = speed;
        this.damage = damage;
        this.isMovingLeft = isMovingLeft;
        this.isDestroyed = isDestroyed;
        this.creatorPlantCategory = creatorPlantCategory;
    }

    public Projectile(double xCoordinate, double yCoordinate, double speed, int damage, boolean isMovingLeft,
                      boolean isDestroyed, Plant creatorPlantCategory, Zombie targetZombie) {
        this(xCoordinate, yCoordinate, speed, damage, isMovingLeft, isDestroyed, creatorPlantCategory);
        this.targetZombie = targetZombie;
    }

    public Projectile(double xCoordinate, double yCoordinate, double speed, int damage, boolean isMovingLeft,
                      boolean isDestroyed, Plant creatorPlantCategory, Tile targetTile) {
        this(xCoordinate, yCoordinate, speed, damage, isMovingLeft, isDestroyed, creatorPlantCategory);
        this.targetTile = targetTile;
    }

    public void move() {
        if (isDestroyed) return;
        if (targetZombie != null && !targetZombie.isDead()) {
            moveToward(targetZombie.getX(), targetZombie.getY());
            return;
        }
        if (targetTile != null && targetTile.isGrave()) {
            moveToward(targetTile.getColumn(), targetTile.getRow());
            return;
        }
        if (isMovingLeft) xCoordinate -= speed;
        else xCoordinate += speed;
    }

    private void moveToward(double targetX, double targetY) {
        double dx = targetX - xCoordinate;
        double dy = targetY - yCoordinate;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance <= speed || distance == 0) {
            xCoordinate = targetX;
            yCoordinate = targetY;
            return;
        }
        xCoordinate += dx / distance * speed;
        yCoordinate += dy / distance * speed;
    }

    public boolean checkPlantCollision(Plant plant) {
        return !isDestroyed
                && plant != null
                && plant.getY() == (int) Math.round(yCoordinate)
                && Math.abs(plant.getX() - xCoordinate) < 0.5;
    }

    public boolean checkZombieCollision(Zombie zombie) {
        if (isDestroyed || zombie == null) return false;
        int lane = (int) Math.round(yCoordinate);
        boolean sameLane = zombie instanceof Zomboss
            ? ((Zomboss) zombie).occupiesLane(lane)
            : zombie.getY() == lane;
        double horizontalDistance = zombie instanceof Zomboss
            ? ((Zomboss) zombie).horizontalDistanceTo(xCoordinate)
            : Math.abs(zombie.getX() - xCoordinate);
        return sameLane && horizontalDistance < 0.5;
    }

    public boolean checkBarrelCollision(Barrel barrel) {
        return !isDestroyed
                && barrel != null
                && barrel.getY() == (int) Math.round(yCoordinate)
                && Math.abs(barrel.getX() - xCoordinate) < 0.5;
    }

    public void destroy() { isDestroyed = true; }

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

    public Plant getCreatorPlantCategory() {
        return creatorPlantCategory;
    }
}
