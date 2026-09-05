package PvZ2.APproject.models.plants.abilities;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.PlantState;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.Zomboss;

public class LobAbility implements PlantAbilityHandler {
    private static final double DEFAULT_PROJECTILE_SPEED = 0.38;
    private final int hitCount;

    public LobAbility(int hitCount) {
        this.hitCount = Math.max(1, hitCount);
    }

    @Override
    public void execute(Plant plant) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) return;
        Zombie target = findClosestZombie(level, plant);
        Tile grave = findClosestGrave(level, plant);
        if (target == null && grave == null) return;
        plant.setState(PlantState.ATTACKING);
        double speed = plant.getData().getProjectileSpeed() > 0
            ? plant.getData().getProjectileSpeed() : DEFAULT_PROJECTILE_SPEED;
        for (int i = 0; i < hitCount; i++) {
            level.getActiveProjectiles().add(new Projectile(
                plant.getX() + 0.15 + i * 0.08,
                plant.getY(),
                speed,
                plant.getDamage(),
                false,
                false,
                plant
            ));
        }
    }

    private Tile findClosestGrave(Level level, Plant plant) {
        for (int col = plant.getX(); col <= level.getGameMap().getColumns(); col++) {
            Tile tile = level.getGameMap().getTile(col, plant.getY());
            if (tile != null && tile.isGrave()) return tile;
        }
        return null;
    }

    private Zombie findClosestZombie(Level level, Plant plant) {
        Zombie target = null;
        double bestDistance = Double.MAX_VALUE;
        for (Zombie zombie : level.getActiveZombies()) {
            if (zombie.isDead()) continue;
            boolean inLane = zombie instanceof Zomboss
                ? ((Zomboss) zombie).occupiesLane(plant.getY())
                : zombie.getY() == plant.getY();
            if (!inLane) continue;
            double distance = zombie instanceof Zomboss
                ? ((Zomboss) zombie).horizontalDistanceTo(plant.getX())
                : zombie.getX() - plant.getX();
            if (distance >= 0 && distance < bestDistance) {
                bestDistance = distance;
                target = zombie;
            }
        }
        return target;
    }
}
