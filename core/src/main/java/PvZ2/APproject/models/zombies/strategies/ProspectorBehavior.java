package PvZ2.APproject.models.zombies.strategies;

import PvZ2.APproject.enums.ZombieEffect;
import PvZ2.APproject.enums.ZombieState;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

public class ProspectorBehavior implements ZombieBehavior {
    private boolean isCastingAbility = true;
    private int abilityTimer = 100;

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
            explodeDynamite(zombie);
            isCastingAbility = false;
            zombie.setAbilityTickTimer(0);
        } else if (zombie.getCurrentState() == ZombieState.WALKING) {
            zombie.walk();
        } else if (zombie.getCurrentState() == ZombieState.WALKING_BACKWARD) {
            walkBackWard(zombie);
        } else if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.attack(targetPlant);
            if(targetPlant.isDead()) {
                zombie.setCurrentState(ZombieState.WALKING_BACKWARD);
            }
        }
    }

    public void explodeDynamite(Zombie zombie) {
        zombie.setY(1);
        zombie.setCurrentState(ZombieState.WALKING_BACKWARD);
    }

    public void walkBackWard(Zombie zombie) {
        zombie.setX(zombie.getX() + zombie.getData().getSpeed() / 10.0);
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
