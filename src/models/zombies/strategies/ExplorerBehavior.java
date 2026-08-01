package models.zombies.strategies;

import enums.PlantTag;
import enums.ZombieEffect;
import enums.ZombieState;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class ExplorerBehavior implements ZombieBehavior {
    private boolean torchOn = true;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        if (zombie.getEffects().contains(ZombieEffect.FROZEN) || zombie.getEffects().contains(ZombieEffect.CHILLED)) {
            torchOn = false;
        }
        if (zombie.getEffects().contains(ZombieEffect.BURNING)) {
            torchOn = true;
        }
        if (zombie.getCurrentState() == ZombieState.EATING && targetPlant != null) {
            if (torchOn && zombie.getX() - targetPlant.getX() <= 1) {
                zombie.destroyPlant(targetPlant);
            } else {
                zombie.attack(targetPlant);
            }
            if (targetPlant.isDead()) {
                zombie.setCurrentState(ZombieState.WALKING);
            }
        } else {
            zombie.walk();
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        Plant creator = projectile.getCreatorPlantCategory();
        if (creator != null && creator.hasThisTag(PlantTag.ICE)) {
            torchOn = false;
        } else if (creator != null && creator.hasThisTag(PlantTag.FIRE)) {
            torchOn = true;
        }
        zombie.takeDamage(projectile.getDamage(), creator);
    }
}
