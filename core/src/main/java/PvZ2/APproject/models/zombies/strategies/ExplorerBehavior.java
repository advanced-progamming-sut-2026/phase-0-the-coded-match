package PvZ2.APproject.models.zombies.strategies;

import PvZ2.APproject.enums.ZombieEffect;
import PvZ2.APproject.enums.ZombieState;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

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
            burn(targetPlant, zombie);
        }
    }

    public void burn(Plant targetPlant, Zombie zombie) {
        if (zombie.getX() - targetPlant.getX() <= 1 && isTorchOn) {
            zombie.destroyPlant(targetPlant);
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
