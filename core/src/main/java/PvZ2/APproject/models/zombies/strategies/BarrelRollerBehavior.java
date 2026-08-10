package models.zombies.strategies;

import enums.ZombieState;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class BarrelRollerBehavior implements ZombieBehavior {

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.attack(targetPlant);
            if(targetPlant.isDead()) {
                zombie.setCurrentState(ZombieState.WALKING);
            }
        } else if (zombie.getCurrentState() == ZombieState.WALKING) {
            zombie.walk();
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
