package models.strategies;

import models.plants.Plant;
import models.zombies.Zombie;

public interface ZombieBehavior {
    void updateZombie(Zombie zombie, Plant targetPlant);
}
