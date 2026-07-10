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
        //TODO: complete when understood plants
        if (zombie.getCurrentState() == ZombieState.WALKING) {
            zombie.walk();
        } else if (zombie.getCurrentState() == ZombieState.EATING) {
            if (targetPlant.getData().getCategory() == PlantCategory.EXPLOSIVE ||
                    targetPlant.getData().getCategory() == PlantCategory.WALL_NUT ||
                    targetPlant.getData().getTags().contains(PlantTag.EXPLOSIVE) ||
                    targetPlant.getData().getTags().contains(PlantTag.MOVE_ZOMBIES)) {
                if (targetPlant.getData().getCategory() == PlantCategory.WALL_NUT && targetPlant.getData().getDisplayName("")) {//TODO: tall-nut name
                    zombie.attack(targetPlant);
                    return;
                }
                zombie.fly(targetPlant);
                zombie.setCurrentState(ZombieState.WALKING);
            } else {
                zombie.attack(targetPlant);
            }
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage());
    }
}
