package models.zombies.strategies;

import models.MiniGameRelated.IZombie;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class SunProducerBehavior implements ZombieBehavior {
    private int tickCounter;
    private int productionTimer = 100;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        tickCounter++;
        if (tickCounter >= productionTimer) {
            zombie.setSunProduced(true);
            tickCounter = 0;

            if (productionTimer > 50) {
                productionTimer -= 10;
            }
        }
    }
    @Override
    public void onProjectileHit (Zombie zombie, Projectile projectile){
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}