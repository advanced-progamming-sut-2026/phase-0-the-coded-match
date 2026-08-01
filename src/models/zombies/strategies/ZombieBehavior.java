package models.zombies.strategies;

import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public interface ZombieBehavior {
    void updateZombie(Zombie zombie, Plant targetPlant);

    void onProjectileHit(Zombie zombie, Projectile projectile);

    default void onDeath(Zombie zombie) {
    }
}
