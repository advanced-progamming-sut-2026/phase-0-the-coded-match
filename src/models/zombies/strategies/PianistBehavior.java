package models.zombies.strategies;

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
            zombie.shuffleZombies(zombie);
            zombie.setAbilityTickTimer(0);
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
