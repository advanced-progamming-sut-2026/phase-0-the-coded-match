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
        Tile tile = GameManagerController.getInstance().getCurrentLevel().getGameMap()
                .getTile((int) Math.round(zombie.getX()), zombie.getY());
        boolean inWater = tile != null && tile.getType() == TileType.WATER;
        if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.setSubmerged(false);
            zombie.attack(targetPlant);
            if (targetPlant == null || targetPlant.isDead()) {
                zombie.setCurrentState(ZombieState.WALKING);
            }
        } else {
            zombie.setSubmerged(inWater);
            zombie.walk();
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        Plant creator = projectile.getCreatorPlantCategory();
        if (!zombie.isSubmerged() || creator != null && creator.getData().getCategory() == PlantCategory.LOBBER) {
            zombie.takeDamage(projectile.getDamage(), creator);
        }
    }
}
