package PvZ2.APproject.models.zombies.strategies;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.PlantCategory;
import PvZ2.APproject.enums.TileType;
import PvZ2.APproject.enums.ZombieState;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

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
            if (projectile.getCreatorPlantCategory().getData().getCategory() == PlantCategory.LOBBER) {
                zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
                projectile.destroy();
            }
        } else {
            zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
        }
    }
}
