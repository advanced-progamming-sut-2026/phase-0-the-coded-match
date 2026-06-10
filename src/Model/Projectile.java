package Model;

public class Projectile {
    private double xCoordinate;
    private double yCoordinate;
    private double speed;
    private int damage;
    private boolean isMovingLeft;
    private boolean isDestroyed;

    public Projectile(double x, double y, double speed, int damage, boolean isMovingLeft) {}

    public void move() {}
    public boolean checkPlantCollision(Plant plant) { return false; }
    public boolean checkZombieCollision(Zombie zombie) { return false; }
}
