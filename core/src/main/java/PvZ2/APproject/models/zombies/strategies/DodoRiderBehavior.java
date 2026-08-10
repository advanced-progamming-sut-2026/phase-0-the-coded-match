package models.zombies.strategies;

import enums.PlantCategory;
import enums.PlantTag;
import enums.ZombieState;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

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
