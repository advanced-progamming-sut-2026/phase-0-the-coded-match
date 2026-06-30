package models.strategies;

import controllers.GameManagerController;
import enums.ZombieState;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class TurquoiseBehavior implements ZombieBehavior {
    private boolean isCastingAbility = false;
    private int abilityTimer = 5;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        if (!isCastingAbility) {
            if (GameManagerController.getCurrentLevel().isPlantWithinDistance(zombie, 4)) {
                zombie.setCurrentState(ZombieState.STEALING);
                isCastingAbility = true;
            } else {
                zombie.walk();
            }
        } else {
            if (zombie.getAbilityTickTimer() == abilityTimer) {
                zombie.lazer();
                zombie.setAbilityTickTimer(0);
                isCastingAbility = false;
                zombie.setCurrentState(ZombieState.WALKING);
            } else {
                zombie.setStolenSuns(zombie.getStolenSuns() + zombie.stealSuns());
                zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
            }
            if (zombie.getCurrentState() == ZombieState.EATING) {
                zombie.attack(targetPlant);
                if(targetPlant.isDead()) {
                    zombie.setCurrentState(ZombieState.WALKING);
                }
            }
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage());
    }
}
