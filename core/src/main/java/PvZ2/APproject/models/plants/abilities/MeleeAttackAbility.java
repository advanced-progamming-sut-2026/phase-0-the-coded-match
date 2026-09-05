package PvZ2.APproject.models.plants.abilities;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

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
                zombie.takeDamage(plant.getDamage(), plant);
            }
        }
        for (int col = plant.getX(); col <= Math.min(level.getGameMap().getColumns(), plant.getX() + range); col++) {
            Tile tile = level.getGameMap().getTile(col, plant.getY());
            if (tile.isGrave()) {
                tile.takeDamage(plant.getDamage());
                break;
            }
        }
    }
}
