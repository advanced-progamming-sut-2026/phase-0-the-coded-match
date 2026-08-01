package models.zombies.strategies;

import controllers.GameManagerController;
import enums.ZombieState;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

import java.util.Random;

public class PianistBehavior implements ZombieBehavior {
    private static final int ABILITY_TICKS = 50;
    private final Random random = new Random();

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
        if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.destroyPlant(targetPlant);
            zombie.setCurrentState(ZombieState.WALKING);
        } else {
            zombie.walk();
        }
        if (zombie.getAbilityTickTimer() >= ABILITY_TICKS) {
            shuffleZombies(zombie);
            zombie.setAbilityTickTimer(0);
        }
    }

    private void shuffleZombies(Zombie pianist) {
        int rows = GameManagerController.getInstance().getCurrentLevel().getGameMap().getRows();
        for (Zombie zombie : GameManagerController.getInstance().getCurrentLevel().getActiveZombies()) {
            if (zombie == pianist || zombie.getY() != pianist.getY()) {
                continue;
            }
            int direction = random.nextBoolean() ? 1 : -1;
            int newRow = zombie.getY() + direction;
            if (newRow < 1 || newRow > rows) {
                newRow = zombie.getY() - direction;
            }
            if (newRow >= 1 && newRow <= rows) {
                zombie.setY(newRow);
            }
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
