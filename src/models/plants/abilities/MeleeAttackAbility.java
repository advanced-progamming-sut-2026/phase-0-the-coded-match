package models.plants.abilities;

import controllers.GameManagerController;
import models.Level;
import models.plants.Plant;
import models.zombies.Zombie;

public class MeleeAttackAbility implements PlantAbilityHandler {
    private final int range;

    public MeleeAttackAbility(int range) {
        this.range = Math.max(1, range);
    }

    @Override
    public void execute(Plant plant) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return;
        }
        for (Zombie zombie : level.getActiveZombies()) {
            if (zombie.getY() == plant.getY() && Math.abs(zombie.getX() - plant.getX()) <= range) {
                zombie.takeDamage(plant.getData().getDamage());
            }
        }
    }
}
