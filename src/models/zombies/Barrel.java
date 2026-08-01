package models.zombies;

import controllers.GameManagerController;
import enums.ZombieState;
import models.Projectile;
import models.Update;
import models.plants.Plant;

public class Barrel implements Update {
    private double x;
    private int y;
    private int currentHp;
    private final int maxHp = 1100;
    private Zombie owner;
    private boolean destroyed;
    private boolean impsSpawned;
    private final boolean spawnImpsOnDestroy;

    public Barrel(double x, int y, Zombie owner) {
        this(x, y, owner, true);
    }

    public Barrel(double x, int y, Zombie owner, boolean spawnImpsOnDestroy) {
        this.x = x;
        this.y = y;
        currentHp = maxHp;
        this.owner = owner;
        this.spawnImpsOnDestroy = spawnImpsOnDestroy;
    }

    public void roll() {
        if (owner != null) {
            x = owner.getX() - 0.5;
        }
    }

    public void takeDamage(int damage) {
        if (destroyed) {
            return;
        }
        currentHp = Math.max(0, currentHp - Math.max(0, damage));
        if (currentHp == 0) {
            destroyBarrel();
        }
    }

    public void destroyBarrel() {
        if (destroyed) {
            return;
        }
        destroyed = true;
        spawnImps();
    }

    public void spawnImps() {
        if (!spawnImpsOnDestroy || impsSpawned || GameManagerController.getInstance().getCurrentLevel() == null) {
            return;
        }
        ZombieData impData = ZombieRepository.getInstance().findById("ZombieImp");
        if (impData == null) {
            return;
        }
        impsSpawned = true;
        GameManagerController.getInstance().getCurrentLevel().getActiveZombies().add(new Zombie(impData, x, y));
        GameManagerController.getInstance().getCurrentLevel().getActiveZombies().add(new Zombie(impData, x, y));
    }

    @Override
    public void update() {
        if (destroyed || GameManagerController.getInstance().getCurrentLevel() == null) {
            return;
        }
        if (owner != null && !owner.isDead()) {
            roll();
        } else {
            owner = null;
        }
        Plant collidedPlant = GameManagerController.getInstance().getCurrentLevel().getPlantAt((int) Math.round(x), y);
        if (collidedPlant != null) {
            collidedPlant.setCurrentHp(0);
            destroyBarrel();
            return;
        }
        for (Zombie zombie : GameManagerController.getInstance().getCurrentLevel().getActiveZombies()) {
            if (zombie != owner && zombie.getCurrentState() == ZombieState.HYPNOTIZED && zombie.getY() == y
                    && Math.abs(zombie.getX() - x) < 0.5) {
                zombie.setCurrentHp(0);
                destroyBarrel();
                return;
            }
        }
    }

    public void onProjectileHit(Projectile projectile) {
        if (projectile != null) {
            takeDamage(projectile.getDamage());
        }
    }

    public boolean isDestroyed() {
        return destroyed;
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
        this.currentHp = Math.max(0, currentHp);
        if (this.currentHp == 0) {
            destroyBarrel();
        }
    }

    public Zombie getOwner() {
        return owner;
    }

    public void setOwner(Zombie owner) {
        this.owner = owner;
    }
}
