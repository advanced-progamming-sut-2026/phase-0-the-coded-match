package models.zombies.strategies;

import controllers.GameManagerController;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class HunterBehavior implements ZombieBehavior {
    private static final int SHOT_TICKS = 50;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
        if (zombie.getAbilityTickTimer() >= SHOT_TICKS) {
            Plant target = GameManagerController.getInstance().getCurrentLevel().getFrontMostPlantInRow(zombie.getY(), zombie.getX());
            if (target != null) {
                target.addFreezeLevel(1);
            }
            zombie.setAbilityTickTimer(0);
        }
        zombie.walk();
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
