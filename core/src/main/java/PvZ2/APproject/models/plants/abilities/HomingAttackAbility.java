package PvZ2.APproject.models.plants.abilities;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.enums.PlantState;
import PvZ2.APproject.models.zombies.Zombie;

public class HomingAttackAbility implements PlantAbilityHandler {
    private final int hitCount;

    public HomingAttackAbility(int hitCount) {
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
            double zombieDistance = target == null ? Double.MAX_VALUE : Math.abs(target.getX() - plant.getX()) + Math.abs(target.getY() - plant.getY());
            double graveDistance = grave == null ? Double.MAX_VALUE : Math.abs(grave.getColumn() - plant.getX()) + Math.abs(grave.getRow() - plant.getY());
            if (target == null && grave == null) {
                return;
            }
            plant.setState(PlantState.ATTACKING);
            if (graveDistance < zombieDistance) grave.takeDamage(plant.getDamage());
            else target.takeDamage(plant.getDamage(), plant);
        }
    }

    private Tile findClosestGrave(Level level, Plant plant) {
        Tile target = null;
        double bestDistance = Double.MAX_VALUE;
        for (int row = 1; row <= level.getGameMap().getRows(); row++) {
            for (int col = plant.getX(); col <= level.getGameMap().getColumns(); col++) {
                Tile tile = level.getGameMap().getTile(col, row);
                double distance = Math.abs(col - plant.getX()) + Math.abs(row - plant.getY());
                if (tile.isGrave() && distance < bestDistance) {
                    bestDistance = distance;
                    target = tile;
                }
            }
        }
        return target;
    }

    private Zombie findClosestZombie(Level level, Plant plant) {
        Zombie target = null;
        double bestDistance = Double.MAX_VALUE;
        for (Zombie zombie : level.getActiveZombies()) {
            if (zombie.isDead()) {
                continue;
            }
            double distance = Math.abs(zombie.getX() - plant.getX()) + Math.abs(zombie.getY() - plant.getY());
            if (distance < bestDistance) {
                bestDistance = distance;
                target = zombie;
            }
        }
        return target;
    }
}
