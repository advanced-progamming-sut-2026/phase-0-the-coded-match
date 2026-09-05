package PvZ2.APproject.models.plants.abilities;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.enums.PlantState;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.Zomboss;

public class ShootAbility implements PlantAbilityHandler {
    private static final double DEFAULT_PROJECTILE_SPEED = 0.5;
    private final int projectileCount;
    private final int damageMultiplier;

    public ShootAbility(int projectileCount) {
        this(projectileCount, 1);
    }

    public ShootAbility(int projectileCount, int damageMultiplier) {
        this.projectileCount = Math.max(1, projectileCount);
        this.damageMultiplier = Math.max(1, damageMultiplier);
    }

    @Override
    public void execute(Plant plant) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null || !hasZombieInLane(level, plant.getY(), plant.getX())) {
            return;
        }
        plant.setState(PlantState.SHOOTING);
        int count = Math.max(projectileCount, plant.getData().getProjectileCount()) * Math.max(1, plant.getStackCount());
        for (int i = 0; i < count; i++) {
            level.getActiveProjectiles().add(createProjectile(plant, plant.getY(), i));
        }
    }

    protected Projectile createProjectile(Plant plant, int lane, int offset) {
        double startX = plant.getX() + 0.15 + (offset * 0.08);
        double speed = plant.getData().getProjectileSpeed() > 0 ? plant.getData().getProjectileSpeed() : DEFAULT_PROJECTILE_SPEED;
        int damage = Math.max(0, plant.getDamage() * damageMultiplier);
        return new Projectile(startX, lane, speed, damage, false, false, plant);
    }

    protected boolean hasZombieInLane(Level level, int lane, int plantX) {
        for (Zombie zombie : level.getActiveZombies()) {
            if (zombie.isDead()) continue;
            boolean inLane = zombie instanceof Zomboss
                ? ((Zomboss) zombie).occupiesLane(lane)
                : zombie.getY() == lane;
            double distance = zombie instanceof Zomboss
                ? ((Zomboss) zombie).horizontalDistanceTo(plantX)
                : zombie.getX() - plantX;
            if (inLane && distance >= 0) return true;
        }
        for (int col = plantX; col <= level.getGameMap().getColumns(); col++) {
            if (level.getGameMap().getTile(col, lane).isGrave()) {
                return true;
            }
        }
        return false;
    }
}
