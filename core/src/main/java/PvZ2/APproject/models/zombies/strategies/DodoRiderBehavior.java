package PvZ2.APproject.models.zombies.strategies;

import PvZ2.APproject.enums.PlantCategory;
import PvZ2.APproject.enums.PlantTag;
import PvZ2.APproject.enums.ZombieState;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

public class DodoRiderBehavior implements ZombieBehavior {
    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        if (zombie.getCurrentState() == ZombieState.WALKING) {
            zombie.walk();
        } else if (zombie.getCurrentState() == ZombieState.EATING) {
            if (targetPlant.getData().getCategory() == PlantCategory.EXPLOSIVE ||
                    targetPlant.getData().getCategory() == PlantCategory.WALL_NUT ||
                    targetPlant.getData().getTags().contains(PlantTag.EXPLOSIVE) ||
                    targetPlant.getData().getTags().contains(PlantTag.MOVE_ZOMBIES)) {
                if (targetPlant.getData().getCategory() == PlantCategory.WALL_NUT &&
                        targetPlant.getData().getDisplayName().equalsIgnoreCase("Tall-nut")) {
                    zombie.attack(targetPlant);
                    return;
                }
                fly(targetPlant, zombie);
                zombie.setCurrentState(ZombieState.WALKING);
            } else {
                zombie.attack(targetPlant);
            }
        }
    }

    public void fly(Plant target, Zombie zombie) {
        zombie.setX(target.getX() + 1); // shifts the zombie by one tile
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
