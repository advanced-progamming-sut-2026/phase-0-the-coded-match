package models.zombies.strategies;

import enums.ZombieEffect;
import enums.ZombieState;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class ExplorerBehavior implements ZombieBehavior {
    private boolean isTorchOn = true;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        if (zombie.getEffects().contains(ZombieEffect.FROZEN)) {
            isTorchOn = false;
        } else if (zombie.getEffects().contains(ZombieEffect.BURNING)) {
            isTorchOn = true;
        }
        if (zombie.getCurrentState() == ZombieState.WALKING) {
            zombie.walk();
        } else {
            zombie.burn(targetPlant);
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
