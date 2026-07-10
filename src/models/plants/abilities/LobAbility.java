package models.plants.abilities;

import controllers.GameManagerController;
import models.Level;
import models.plants.Plant;
import models.zombies.Zombie;

public class LobAbility implements PlantAbilityHandler {
    private final int hitCount;

    public LobAbility(int hitCount) {
        this.hitCount = Math.max(1, hitCount);
    }

    @Override
    public void execute(Plant plant) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return;
        }
        for (int i = 0; i < hitCount; i++) {
            Zombie target = findClosestZombie(level, plant);
            if (target == null) {
                return;
            }
            target.takeDamage(plant.getData().getDamage());
        }
    }

    private Zombie findClosestZombie(Level level, Plant plant) {
        Zombie target = null;
        double bestDistance = Double.MAX_VALUE;
        for (Zombie zombie : level.getActiveZombies()) {
            if (zombie.getY() != plant.getY() || zombie.isDead()) {
                continue;
            }
            double distance = zombie.getX() - plant.getX();
            if (distance >= 0 && distance < bestDistance) {
                bestDistance = distance;
                target = zombie;
            }
        }
        return target;
    }
}
