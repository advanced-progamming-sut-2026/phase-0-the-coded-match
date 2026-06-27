package models.strategies;

import enums.ZombieState;
import models.plants.Plant;
import models.zombies.Zombie;

public class GiantBehavior implements ZombieBehavior {
    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.destroyPlant(targetPlant);
        } else if (zombie.getCurrentState() == ZombieState.WALKING) {
            zombie.walk();
        }
        if (zombie.getCurrentHp() <= (zombie.getData().getMaxHP() / 2) && !zombie.isHasThrownImp()) {
            zombie.throwImp();
            zombie.setHasThrownImp(true);
        }
    }
}
