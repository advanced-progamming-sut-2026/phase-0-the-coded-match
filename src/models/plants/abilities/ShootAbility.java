package models.plants.abilities;

import controllers.GameManagerController;
import models.Level;
import models.Projectile;
import models.plants.Plant;

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
        int count = Math.max(projectileCount, plant.getData().getProjectileCount());
        for (int i = 0; i < count; i++) {
            level.getActiveProjectiles().add(createProjectile(plant, plant.getY(), i));
        }
    }

    protected Projectile createProjectile(Plant plant, int lane, int offset) {
        double startX = plant.getX() + 0.15 + (offset * 0.08);
        double speed = plant.getData().getProjectileSpeed() > 0 ? plant.getData().getProjectileSpeed() : DEFAULT_PROJECTILE_SPEED;
        int damage = Math.max(0, plant.getData().getDamage() * damageMultiplier);
        return new Projectile(startX, lane, speed, damage, false, false, plant.getData().getCategory().toString());
    }

    protected boolean hasZombieInLane(Level level, int lane, int plantX) {
        for (models.zombies.Zombie zombie : level.getActiveZombies()) {
            if (zombie.getY() == lane && zombie.getX() >= plantX && !zombie.isDead()) {
                return true;
            }
        }
        return false;
    }
}
