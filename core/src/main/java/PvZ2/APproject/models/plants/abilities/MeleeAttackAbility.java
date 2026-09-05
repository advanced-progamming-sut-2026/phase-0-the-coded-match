package PvZ2.APproject.models.plants.abilities;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.enums.PlantState;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.Zomboss;

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
        boolean attacked = false;
        for (Zombie zombie : level.getActiveZombies()) {
            boolean inLane = zombie instanceof Zomboss
                ? ((Zomboss) zombie).occupiesLane(plant.getY())
                : zombie.getY() == plant.getY();
            double distance = zombie instanceof Zomboss
                ? ((Zomboss) zombie).horizontalDistanceTo(plant.getX())
                : Math.abs(zombie.getX() - plant.getX());
            if (inLane && distance <= range) {
                zombie.takeDamage(plant.getDamage(), plant);
                attacked = true;
            }
        }
        for (int col = plant.getX(); col <= Math.min(level.getGameMap().getColumns(), plant.getX() + range); col++) {
            Tile tile = level.getGameMap().getTile(col, plant.getY());
            if (tile != null && tile.isGrave()) {
                tile.takeDamage(plant.getDamage());
                attacked = true;
                break;
            }
        }
        if (attacked) plant.setState(PlantState.ATTACKING);
    }
}
