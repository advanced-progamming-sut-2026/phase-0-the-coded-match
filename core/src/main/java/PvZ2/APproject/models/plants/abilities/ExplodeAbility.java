package PvZ2.APproject.models.plants.abilities;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.enums.PlantState;
import PvZ2.APproject.models.zombies.Zombie;

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
            if (Math.abs(zombie.getY() - plant.getY()) <= 1 && Math.abs(zombie.getX() - plant.getX()) <= 1.5) {
                zombie.takeDamage(damage, plant);
            }
        }
        for (int row = Math.max(1, plant.getY() - 1); row <= Math.min(level.getGameMap().getRows(), plant.getY() + 1); row++) {
            for (int col = Math.max(1, plant.getX() - 1); col <= Math.min(level.getGameMap().getColumns(), plant.getX() + 1); col++) {
                Tile tile = level.getGameMap().getTile(col, row);
                if (tile.isGrave()) tile.takeDamage(damage);
            }
        }
        plant.setCurrentHp(0);
    }
}
