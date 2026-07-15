package models.plants.abilities;

import controllers.GameManagerController;
import models.Level;
import models.plants.Plant;
import models.zombies.Zombie;

public class ExplodeAbility implements PlantAbilityHandler {
    private final int damageMultiplier;
    private final boolean isExplosive = true;

    public ExplodeAbility(int damageMultiplier) {
        this.damageMultiplier = Math.max(1, damageMultiplier);
    }

    @Override
    public void execute(Plant plant) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return;
        }
        int damage = Math.max(0, plant.getData().getDamage() * damageMultiplier);
        for (Zombie zombie : level.getActiveZombies().toArray(new Zombie[0])) {
            if (Math.abs(zombie.getY() - plant.getY()) <= 1 && Math.abs(zombie.getX() - plant.getX()) <= 1.5) {
                zombie.takeDamage(damage, plant);
            }
        }
        plant.setCurrentHp(0);
    }
}
