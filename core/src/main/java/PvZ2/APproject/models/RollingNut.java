package PvZ2.APproject.models;

import PvZ2.APproject.enums.BowlingNutType;
import PvZ2.APproject.models.zombies.Zombie;

public class RollingNut {
    private static final double MIN_Y = 1.0;
    private static final double MAX_Y = 5.0;
    private static final double COLLISION_DISTANCE = 0.45;

    private final BowlingNutType nutType;
    private double xCoordinate;
    private double yCoordinate;
    private final double speed;
    private final int damage;
    private double movementAngle;
    private boolean firstCollision;
    private boolean destroyed;
    private Zombie lastHitZombie;

    public RollingNut(BowlingNutType type, double startX, double startY) {
        this.nutType = type;
        this.xCoordinate = startX;
        this.yCoordinate = startY;
        this.speed = type == BowlingNutType.GIANT_WALLNUT ? 0.13 : 0.16;
        this.damage = type == BowlingNutType.EXPLODE_O_NUT ? 1800 : 200;
        this.movementAngle = 0.0;
        this.firstCollision = true;
        this.destroyed = false;
    }

    public void advancePosition() {
        if (destroyed) {
            return;
        }
        double radians = Math.toRadians(movementAngle);
        xCoordinate += speed * Math.cos(radians);
        yCoordinate += speed * Math.sin(radians);
        reflectFromScreenBoundary();
    }

    public void reflectFromScreenBoundary() {
        if (yCoordinate < MIN_Y) {
            yCoordinate = MIN_Y + (MIN_Y - yCoordinate);
            movementAngle = -movementAngle;
        } else if (yCoordinate > MAX_Y) {
            yCoordinate = MAX_Y - (yCoordinate - MAX_Y);
            movementAngle = -movementAngle;
        }
    }

    public void reflectFromZombieCollision() {
        if (firstCollision) {
            movementAngle = movementAngle <= 0 ? 45.0 : -45.0;
            firstCollision = false;
        } else {
            movementAngle = -movementAngle;
        }
    }

    public void applyRegularDamage(Zombie zombie) {
        if (zombie == null || destroyed) {
            return;
        }
        zombie.setCurrentHp(Math.max(0, zombie.getCurrentHp() - damage));
        reflectFromZombieCollision();
        lastHitZombie = zombie;
    }

    public void triggerExplosionImpact() {
        destroyed = true;
    }

    public void crushZombieAndMaintainPath(Zombie zombie) {
        if (zombie == null || destroyed) {
            return;
        }
        zombie.setCurrentHp(0);
        lastHitZombie = zombie;
    }

    public boolean collidesWith(Zombie zombie) {
        if (zombie == null || zombie == lastHitZombie || zombie.isDead()) {
            return false;
        }
        double dx = xCoordinate - zombie.getX();
        double dy = yCoordinate - zombie.getY();
        return Math.hypot(dx, dy) <= COLLISION_DISTANCE;
    }

    public void clearLastHitWhenSeparated() {
        if (lastHitZombie == null) {
            return;
        }
        double dx = xCoordinate - lastHitZombie.getX();
        double dy = yCoordinate - lastHitZombie.getY();
        if (Math.hypot(dx, dy) > COLLISION_DISTANCE * 1.5) {
            lastHitZombie = null;
        }
    }

    public BowlingNutType getNutType() { return nutType; }
    public double getXCoordinate() { return xCoordinate; }
    public double getYCoordinate() { return yCoordinate; }
    public double getMovementAngle() { return movementAngle; }
    public boolean isDestroyed() { return destroyed; }
    public void setDestroyed(boolean destroyed) { this.destroyed = destroyed; }
}
