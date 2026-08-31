package PvZ2.APproject.models.zombies.strategies;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.ZombieState;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

import java.util.Random;

public class PianistBehavior implements ZombieBehavior {
    private int abilityTimer = 50;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
        if (zombie.getCurrentState() == ZombieState.EATING) {
            if (targetPlant != null) zombie.destroyPlant(targetPlant);
            zombie.setCurrentState(ZombieState.WALKING);
        } else if (zombie.getCurrentState() == ZombieState.WALKING) {
            zombie.walk();
        }
        if (zombie.getAbilityTickTimer() == abilityTimer) {
            shuffleZombies(zombie);
            zombie.setAbilityTickTimer(0);
        }
    }

    public void shuffleZombies(Zombie pianist) {
        int totalRows = GameManagerController.getInstance().getCurrentLevel().getGameMap().getRows();
        if (totalRows <= 1) return;
        Random random = new Random();
        for (Zombie zombie : new java.util.ArrayList<>(GameManagerController.getInstance().getCurrentLevel().getActiveZombies())) {
            if (zombie != pianist) {
                int currentRow = zombie.getY();
                int newRow = 1 + random.nextInt(totalRows - 1);
                if (newRow >= currentRow) newRow++;
                zombie.setY(newRow);
            }
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
