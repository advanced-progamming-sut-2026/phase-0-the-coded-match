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
        if (zombie.getCurrentState() != ZombieState.EATING || targetPlant == null) {
            zombie.walk();
            return;
        }
        boolean obstacle = targetPlant.getData().getCategory() == PlantCategory.EXPLOSIVE
                || targetPlant.getData().getCategory() == PlantCategory.WALL_NUT
                || targetPlant.hasThisTag(PlantTag.EXPLOSIVE)
                || targetPlant.hasThisTag(PlantTag.MOVE_ZOMBIES);
        boolean tallNut = targetPlant.getData().getDisplayName().equalsIgnoreCase("Tall-nut");
        if (obstacle && !tallNut) {
            zombie.setX(Math.max(0, targetPlant.getX() - 1));
            zombie.setCurrentState(ZombieState.WALKING);
        } else {
            zombie.attack(targetPlant);
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
