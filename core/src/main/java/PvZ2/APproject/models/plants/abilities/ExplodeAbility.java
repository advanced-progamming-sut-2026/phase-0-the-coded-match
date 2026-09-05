package PvZ2.APproject.models.plants.abilities;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.enums.PlantState;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.Zomboss;

public class ExplodeAbility implements PlantAbilityHandler {
    private final int damageMultiplier;
    private final boolean isExplosive = true;

    public ExplodeAbility(int damageMultiplier) {
        this.damageMultiplier = Math.max(1, damageMultiplier);
    }

    @Override
    public void execute(Plant plant) {
        plant.setState(PlantState.EXPLODING);
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return;
        }
        int damage = Math.max(0, plant.getDamage() * damageMultiplier);
        for (Zombie zombie : level.getActiveZombies().toArray(new Zombie[0])) {
            boolean rowInRange = zombie instanceof Zomboss
                ? ((Zomboss) zombie).occupiesLane(plant.getY())
                    || ((Zomboss) zombie).occupiesLane(plant.getY() - 1)
                    || ((Zomboss) zombie).occupiesLane(plant.getY() + 1)
                : Math.abs(zombie.getY() - plant.getY()) <= 1;
            double distance = zombie instanceof Zomboss
                ? ((Zomboss) zombie).horizontalDistanceTo(plant.getX())
                : Math.abs(zombie.getX() - plant.getX());
            if (rowInRange && distance <= 1.5) zombie.takeDamage(damage, plant);
        }
        for (int row = Math.max(1, plant.getY() - 1); row <= Math.min(level.getGameMap().getRows(), plant.getY() + 1);
             row++) {
            for (int col = Math.max(1, plant.getX() - 1); col <= Math.min(level.getGameMap().getColumns(), plant.getX()
                + 1); col++) {
                Tile tile = level.getGameMap().getTile(col, row);
                if (tile.isGrave()) tile.takeDamage(damage);
            }
        }
        plant.setCurrentHp(0);
    }
}
