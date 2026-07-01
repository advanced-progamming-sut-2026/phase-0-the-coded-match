package models.strategies;

import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class KingBehavior implements ZombieBehavior {
    private int abilityTimer = 3;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
        if (zombie.getAbilityTickTimer() == abilityTimer) {
            zombie.makeKnight();
            zombie.setAbilityTickTimer(0);
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage());
    }
}
