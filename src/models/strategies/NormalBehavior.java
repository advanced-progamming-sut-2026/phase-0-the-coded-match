package models.strategies;

import enums.ZombieState;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class NormalBehavior implements ZombieBehavior {
    private int abilityTimer = 6;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        if (zombie.getData().getId().equalsIgnoreCase("ZombieArmZombieNewspaper")) {
            if (zombie.getArmors().isEmpty()) {
                zombie.setCurrentState(ZombieState.RUNNING);
            }
        } else if (zombie.getData().getId().equalsIgnoreCase("ZombieRa")) {
            zombie.stealDroppedSuns();
        } else if (zombie.getData().getId().equalsIgnoreCase("ZombieTombRaiser")) {
            zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
            if (zombie.getAbilityTickTimer() == abilityTimer) {
                zombie.raiseTomb();
                zombie.setAbilityTickTimer(0);
            }
        } else if ((zombie.getData().getId().equalsIgnoreCase("ZombieIceAgeHunter") ||
                (zombie.getData().getId().equalsIgnoreCase("ZombieBeachOctopus"))) && targetPlant != null) {
            zombie.shootProjectile();
        }

        if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.attack(targetPlant);
            if(targetPlant.isDead()) {
                zombie.setCurrentState(ZombieState.WALKING);
            }
        } else if (zombie.getCurrentState() == ZombieState.WALKING) {
            zombie.walk();
        } else if (zombie.getCurrentState() == ZombieState.RUNNING) {
            zombie.run();
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        if (zombie.getData().getId().equalsIgnoreCase("ZombieDarkImpDragon")) {
            if (projectile.) {//TODO: if the type was fiery
                return;
            }
        }
        zombie.takeDamage(projectile.getDamage());
    }
}
