package PvZ2.APproject.models.zombies;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.GameMapRelated.Tile;
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
        if (GameManagerController.getInstance().getCurrentLevel() == null) return;
        if (GameManagerController.getInstance().getCurrentLevel().getBarrels().remove(this)) {
            spawnImps();
        }
    }

    public void spawnImps() {
        ZombieData impData = ZombieRepository.getInstance().findById("ZombieImp");
        if (impData == null) impData = ZombieRepository.getInstance().findByDisplayName("Imp");
        if (impData == null || GameManagerController.getInstance().getCurrentLevel() == null) return;
        GameManagerController.getInstance().getCurrentLevel().getActiveZombies().add(new Zombie(impData, x, y));
        GameManagerController.getInstance().getCurrentLevel().getActiveZombies().add(new Zombie(impData, x, y));
    }

    @Override
    public void update(float delta) {
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
        if (GameManagerController.getInstance().getCurrentLevel() == null) return;
        GameManagerController.getInstance().getCurrentLevel().getActivePlants().remove(plant);
        Tile tile = GameManagerController.getInstance().getCurrentLevel().getGameMap().getTile(plant.getX(), plant.getY());
        if (tile != null) {
            if (tile.getPlant() == plant) tile.removePlant();
            if (tile.getLilyPadPlant() == plant) tile.setLilyPadPlant(null);
        }
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
