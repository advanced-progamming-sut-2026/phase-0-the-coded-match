package PvZ2.APproject.models.plants.abilities;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.enums.PlantState;
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
            Tile grave = findClosestGrave(level, plant);
            double zombieDistance = target == null ? Double.MAX_VALUE : target.getX() - plant.getX();
            double graveDistance = grave == null ? Double.MAX_VALUE : grave.getColumn() - plant.getX();
            if (target == null && grave == null) {
                return;
            }
            plant.setState(PlantState.ATTACKING);
            if (graveDistance < zombieDistance) grave.takeDamage(plant.getDamage());
            else target.takeDamage(plant.getDamage(), plant);
        }
    }

    private Tile findClosestGrave(Level level, Plant plant) {
        for (int col = plant.getX(); col <= level.getGameMap().getColumns(); col++) {
            Tile tile = level.getGameMap().getTile(col, plant.getY());
            if (tile.isGrave()) return tile;
        }
        return null;
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
