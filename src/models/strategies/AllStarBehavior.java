package models.strategies;

import enums.ZombieState;
import models.plants.Plant;
import models.zombies.Zombie;

public class AllStarBehavior implements ZombieBehavior {
    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        if (zombie.getCurrentState() == ZombieState.RUNNING && (targetPlant.hasThisTag() ||  ))//TODO: plant is hypnotized
    }
}
