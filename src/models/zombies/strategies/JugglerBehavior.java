package models.zombies.strategies;

import enums.PlantCategory;
import enums.PlantTag;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class JugglerBehavior implements ZombieBehavior {
    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        zombie.walk();
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        Plant creator = projectile.getCreatorPlantCategory();
        if (creator != null && creator.getData().getCategory() != PlantCategory.LOBBER) {
            if (creator.hasThisTag(PlantTag.ICE)) {
                creator.addFreezeLevel(1);
            } else {
                creator.takeDamage(projectile.getDamage());
            }
            projectile.destroy();
            return;
        }
        zombie.takeDamage(projectile.getDamage(), creator);
    }
}
