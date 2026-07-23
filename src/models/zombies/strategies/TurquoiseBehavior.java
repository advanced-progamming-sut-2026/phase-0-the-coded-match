package models.zombies.strategies;

import controllers.GameManagerController;
import enums.ZombieState;
import models.GameMapRelated.GameMap;
import models.GameMapRelated.Tile;
import models.Level;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class TurquoiseBehavior implements ZombieBehavior {
    private boolean isCastingAbility = false;
    private int abilityTimer = 5;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        if (!isCastingAbility) {
            if (GameManagerController.getInstance().getCurrentLevel().isPlantWithinDistance(zombie, 4)) {
                zombie.setCurrentState(ZombieState.STEALING);
                isCastingAbility = true;
            } else {
                zombie.walk();
            }
        } else {
            if (zombie.getAbilityTickTimer() == abilityTimer) {
                lazer(zombie);
                zombie.setAbilityTickTimer(0);
                isCastingAbility = false;
                zombie.setCurrentState(ZombieState.WALKING);
            } else {
                zombie.setStolenSuns(zombie.getStolenSuns() + stealSuns());
                zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
            }
            if (zombie.getCurrentState() == ZombieState.EATING) {
                zombie.attack(targetPlant);
                if(targetPlant.isDead()) {
                    zombie.setCurrentState(ZombieState.WALKING);
                }
            }
        }
    }

    public void lazer(Zombie zombie) {
        GameMap map = GameManagerController.getInstance().getCurrentLevel().getGameMap();

        for (int i = 0; i < map.getColumns(); i++) {
            for (int j = 0; j < map.getRows(); j++) {
                if (j == zombie.getY() && zombie.getX() - i <= 4) {
                    Tile tile = map.getTile(j, i);
                    if (!tile.isEmpty()) {
                        tile.getPlant().setCurrentHp(0);
                    }
                }
            }
        }
    }

    public int stealSuns() {
        int sunsToSteal = Math.max(25, GameManagerController.getInstance().getCurrentLevel().getCollectedSunsAmount());
        if (sunsToSteal > 0) {
            Level level = GameManagerController.getInstance().getCurrentLevel();
            level.setCollectedSunsAmount(level.getCollectedSunsAmount() - sunsToSteal);
        }
        return sunsToSteal;
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
