package PvZ2.APproject.models.zombies.strategies;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.ZombieState;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

public class AllStarBehavior implements ZombieBehavior {
    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        Zombie targetZombie = GameManagerController.getInstance().getCurrentLevel().getAdjacentZombie(zombie);
        if (zombie.getCurrentState() == ZombieState.RUNNING && targetZombie != null
                && targetZombie.getCurrentState() == ZombieState.HYPNOTIZED) {
            targetZombie.setCurrentHp(0);
        }
        if (zombie.getCurrentState() == ZombieState.EATING) {
            if (targetPlant == null) {
                zombie.setCurrentState(ZombieState.WALKING);
                return;
            }
            if (zombie.isWasRunning()) {
                zombie.destroyPlant(targetPlant);
                zombie.setWasRunning(false);
            } else {
                zombie.attack(targetPlant);
            }
            if (targetPlant == null || targetPlant.isDead()) {
                zombie.setCurrentState(ZombieState.WALKING);
            }
        } else if (zombie.getCurrentState() == ZombieState.RUNNING) {
            zombie.run();
        } else {
            zombie.walk();
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
