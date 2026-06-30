package models.strategies;

import enums.ZombieEffect;
import enums.ZombieState;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class ProspectorBehavior implements ZombieBehavior {
    private boolean isCastingAbility = true;
    private int abilityTimer = 10;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        if (isCastingAbility) {
            zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
        }
        if (zombie.getEffects().contains(ZombieEffect.FROZEN) && isCastingAbility) {
            isCastingAbility = false;
            zombie.setAbilityTickTimer(0);
        }
        if (zombie.getAbilityTickTimer() == abilityTimer) {
            zombie.explodeDynamite();
            isCastingAbility = false;
            zombie.setAbilityTickTimer(0);
        } else if (zombie.getCurrentState() == ZombieState.WALKING) {
            zombie.walk();
        } else if (zombie.getCurrentState() == ZombieState.WALKING_BACKWARD) {
            zombie.walkBackWard();
        } else if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.attack(targetPlant);
            if(targetPlant.isDead()) {
                zombie.setCurrentState(ZombieState.WALKING_BACKWARD);
            }
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage());
    }
}
