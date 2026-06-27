package models.strategies;

import enums.ZombieState;
import models.plants.Plant;
import models.zombies.Zombie;

public class NormalBehavior implements ZombieBehavior {
    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.attack(targetPlant);
        } else if (zombie.getCurrentState() == ZombieState.WALKING) {
            zombie.walk();
        }
    }
}
