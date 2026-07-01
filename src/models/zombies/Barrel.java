package models.zombies;

import controllers.GameManagerController;
import models.Update;

public class Barrel implements Update {
    private static double x;
    int y;
    int currentHp;

    public Barrel(double x, int y, int currentHp) {
        this.x = x;
        this.y = y;
        this.currentHp = currentHp;
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
        Zombie imp = new Zombie(ZombieRepository.getInstance().findById("ZombieImp"), x, y);
        GameManagerController.getInstance().getCurrentLevel().getActiveZombies().add(imp);
        GameManagerController.getInstance().getCurrentLevel().getActiveZombies().add(imp);
    }

    @Override
    public void update() {

    }


}
