package models.zombies.strategies;

import controllers.GameManagerController;
import enums.ZombieState;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class PianistBehavior implements ZombieBehavior {
    private int abilityTimer = 5; //TODO: optional?

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
        if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.destroyPlant(targetPlant);
            zombie.setCurrentState(ZombieState.WALKING);
        } else if (zombie.getCurrentState() == ZombieState.WALKING) {
            zombie.walk();
        }
        if (zombie.getAbilityTickTimer() == abilityTimer) {
            shuffleZombies(zombie);
            zombie.setAbilityTickTimer(0);
        }
    }

    public void shuffleZombies(Zombie pianist) {
        for (Zombie zombie : GameManagerController.getInstance().getCurrentLevel().getActiveZombies()) {
            if (zombie != pianist) {
                //TODO: complete this
            }
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
