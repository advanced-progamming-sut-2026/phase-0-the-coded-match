package PvZ2.APproject.models.zombies;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.Update;
import PvZ2.APproject.models.plants.Plant;

public class Barrel implements Update {
    private double x;
    int y;
    int currentHp;
    int maxHp = 1100;
    private Zombie owner;

    public Barrel(double x, int y, Zombie owner) {
        this.x = x;
        this.y = y;
        this.currentHp = maxHp;
        this.owner = owner;
    }

    public void roll() {
        this.x = owner.getX() - 0.5;
    }

    public void takeDamage(int damage) {
        currentHp -= damage;
        if (currentHp <= 0) {
            currentHp = 0;
            destroyBarrel();
        }
    }

    public void destroyBarrel() {
        GameManagerController.getInstance().getCurrentLevel().getBarrels().remove(this);
        spawnImps();
    }

    public void spawnImps() {
        Zombie imp1 = new Zombie(ZombieRepository.getInstance().findById("ZombieImp"), x, y);
        Zombie imp2 = new Zombie(ZombieRepository.getInstance().findById("ZombieImp"), x, y);
        GameManagerController.getInstance().getCurrentLevel().getActiveZombies().add(imp1);
        GameManagerController.getInstance().getCurrentLevel().getActiveZombies().add(imp2);
    }

    @Override
    public void update() {
        if (owner != null && !owner.isDead()) {
            roll();
        }

        Plant collidedPlant = GameManagerController.getInstance().getCurrentLevel().getPlantAt((int) (this.x), this.y);
        if (collidedPlant != null) {
            destroyPlant(collidedPlant);
        }

    }

    public void destroyPlant(Plant plant) {
        plant.setCurrentHp(0);
        GameManagerController.getInstance().getCurrentLevel().getActivePlants().remove(plant); // TODO: After plant dies we need to print "Plant <type> at (<x>, <y>) is destroyed."; but how do we send it to view?
    }

    public void onProjectileHit(Projectile projectile) {
        takeDamage(projectile.getDamage());
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }
    public Zombie getOwner() {
        return owner;
    }

    public void setOwner(Zombie owner) {
        this.owner = owner;
    }
}
