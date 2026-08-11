package PvZ2.APproject.models.plants.abilities;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

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
            target.takeDamage(plant.getData().getDamage(), plant);
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
