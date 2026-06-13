package models;

import enums.BowlingNutType;

public class RollingNut {
    private BowlingNutType nutType;
    private double xCoordinate;
    private double yCoordinate;
    private double speed;
    private int damage;
    private double movementAngle;
    private boolean isFirstCollision;
    private boolean isDestroyed;

    public RollingNut(BowlingNutType type, double startX, double startY) {}


    public void advancePosition() {}
    public void reflectFromScreenBoundary() {}
    public void reflectFromZombieCollision() {}
    public void applyRegularDamage(Zombie zombie) {}
    public void triggerExplosionImpact() {}
    public void crushZombieAndMaintainPath(Zombie zombie) {}
}
