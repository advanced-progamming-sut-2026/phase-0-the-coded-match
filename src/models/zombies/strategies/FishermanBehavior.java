package models.zombies.strategies;

import controllers.GameManagerController;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class FishermanBehavior implements ZombieBehavior {
    private static final int HOOK_TICKS = 50;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
        if (zombie.getAbilityTickTimer() >= HOOK_TICKS) {
            hook(zombie);
            zombie.setAbilityTickTimer(0);
        }
    }

    private void hook(Zombie zombie) {
        Plant target = GameManagerController.getInstance().getCurrentLevel().getFrontMostPlantInRow(zombie.getY(), zombie.getX());
        if (target == null) {
            return;
        }
        int newX = Math.min(GameManagerController.getInstance().getCurrentLevel().getGameMap().getColumns(), target.getX() + 1);
        if (newX >= zombie.getX() - 0.5) {
            target.setCurrentHp(0);
            return;
        }
        target.setPosition(newX, target.getY());
        GameManagerController.getInstance().getCurrentLevel().rebuildPlantTiles();
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
