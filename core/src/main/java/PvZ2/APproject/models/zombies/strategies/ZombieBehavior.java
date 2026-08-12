package PvZ2.APproject.models.zombies.strategies;

import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

public interface ZombieBehavior {
    void updateZombie(Zombie zombie, Plant targetPlant);

    void onProjectileHit(Zombie zombie, Projectile projectile);

    default void onDeath(Zombie zombie) {
    }
}
