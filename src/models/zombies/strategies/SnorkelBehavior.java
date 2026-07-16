package models.zombies.strategies;

import controllers.GameManagerController;
import enums.TileType;
import enums.ZombieState;
import models.GameMapRelated.Tile;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class SnorkelBehavior implements ZombieBehavior {
    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        Tile currentTile = GameManagerController.getInstance().getCurrentLevel().getGameMap().getTile((int)zombie.getX()
                , zombie.getY());

        if (currentTile.getType() == TileType.WATER && zombie.getCurrentState() != ZombieState.EATING) {
            zombie.setSubmerged(true);
        } else if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.attack(targetPlant);
            zombie.setSubmerged(false);
        } else if (zombie.getCurrentState() == ZombieState.WALKING) {
            zombie.walk();
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        if (zombie.isSubmerged()) {
            if (projectile.) {//TODO: projectile is from a lobber
                zombie.takeDamage(projectile.getDamage());
                projectile.destroy();
            }
        } else {
            zombie.takeDamage(projectile.getDamage());
        }
    }
}
