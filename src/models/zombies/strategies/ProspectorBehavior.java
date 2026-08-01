package models.zombies.strategies;

import controllers.GameManagerController;
import enums.PlantTag;
import enums.ZombieEffect;
import enums.ZombieState;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class ProspectorBehavior implements ZombieBehavior {
    private static final int EXPLOSION_TICKS = 100;
    private boolean dynamiteActive = true;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        if (dynamiteActive) {
            zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
            if (zombie.getEffects().contains(ZombieEffect.FROZEN)) {
                dynamiteActive = false;
                zombie.setAbilityTickTimer(0);
            } else if (zombie.getAbilityTickTimer() >= EXPLOSION_TICKS) {
                zombie.setX(1);
                zombie.setCurrentState(ZombieState.WALKING_BACKWARD);
                dynamiteActive = false;
            }
        }
        if (zombie.getCurrentState() == ZombieState.WALKING_BACKWARD) {
            Plant backwardTarget = getNearestPlantToRight(zombie);
            if (backwardTarget != null && backwardTarget.getX() - zombie.getX() <= 0.5) {
                zombie.attack(backwardTarget);
            } else {
                double limit = GameManagerController.getInstance().getCurrentLevel().getGameMap().getColumns();
                zombie.setX(Math.min(limit, zombie.getX() + zombie.getData().getSpeed()));
            }
        } else if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.attack(targetPlant);
            if (targetPlant == null || targetPlant.isDead()) {
                zombie.setCurrentState(ZombieState.WALKING);
            }
        } else {
            zombie.walk();
        }
    }

    private Plant getNearestPlantToRight(Zombie zombie) {
        Plant nearest = null;
        double distance = Double.MAX_VALUE;
        for (Plant plant : GameManagerController.getInstance().getCurrentLevel().getActivePlants()) {
            if (plant.getY() != zombie.getY() || plant.getX() < zombie.getX()) {
                continue;
            }
            double currentDistance = plant.getX() - zombie.getX();
            if (currentDistance < distance) {
                distance = currentDistance;
                nearest = plant;
            }
        }
        return nearest;
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        Plant creator = projectile.getCreatorPlantCategory();
        if (creator != null && creator.hasThisTag(PlantTag.ICE)) {
            dynamiteActive = false;
            zombie.setAbilityTickTimer(0);
        }
        zombie.takeDamage(projectile.getDamage(), creator);
    }
}
