package models.strategies;

import controllers.GameManagerController;
import enums.ZombieState;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class AllStarBehavior implements ZombieBehavior {
    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        Zombie targetZombie = GameManagerController.getCurrentLevel().getAdjacentZombie(zombie);
        if (targetZombie != null && zombie.getCurrentState() == ZombieState.RUNNING) {
            if (targetZombie.getCurrentState() == ZombieState.HYPNOTIZED) {
                zombie.destroyZombie(targetZombie);
            }
        } else if (targetPlant != null && zombie.isWasRunning()) {
            zombie.setWasRunning(false);
            if (targetPlant.hasThisTag()) {//TODO: plant is hypnotized
                zombie.destroyPlant(targetPlant);
            }
        } else if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.attack(targetPlant);
            if(targetPlant.isDead()) {
                zombie.setCurrentState(ZombieState.WALKING);
            }
        } else if (zombie.getCurrentState() == ZombieState.RUNNING) {
            zombie.run();
        } else if (zombie.getCurrentState() == ZombieState.WALKING) {
            zombie.walk();
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage());
    }
}
