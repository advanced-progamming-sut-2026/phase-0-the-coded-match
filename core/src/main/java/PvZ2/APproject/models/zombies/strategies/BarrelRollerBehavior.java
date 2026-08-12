package PvZ2.APproject.models.zombies.strategies;

import PvZ2.APproject.enums.ZombieState;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

public class BarrelRollerBehavior implements ZombieBehavior {
    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.attack(targetPlant);
            if (targetPlant == null || targetPlant.isDead()) {
                zombie.setCurrentState(ZombieState.WALKING);
            }
        } else {
            zombie.walk();
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
