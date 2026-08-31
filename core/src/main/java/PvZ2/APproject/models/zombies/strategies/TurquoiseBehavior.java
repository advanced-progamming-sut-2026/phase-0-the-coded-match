package PvZ2.APproject.models.zombies.strategies;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.ZombieState;
import PvZ2.APproject.models.GameMapRelated.GameMap;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

public class TurquoiseBehavior implements ZombieBehavior {
    private boolean isCastingAbility = false;
    private int abilityTimer = 10;

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
                if (zombie.getAbilityTickTimer() % 5 == 0) {
                    zombie.setStolenSuns(zombie.getStolenSuns() + stealSuns());
                }
                zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
            }
            if (zombie.getCurrentState() == ZombieState.EATING) {
                if (targetPlant == null) {
                    zombie.setCurrentState(ZombieState.WALKING);
                    return;
                }
                zombie.attack(targetPlant);
                if(targetPlant.isDead()) {
                    zombie.setCurrentState(ZombieState.WALKING);
                }
            }
        }
    }

    public void lazer(Zombie zombie) {
        GameMap map = GameManagerController.getInstance().getCurrentLevel().getGameMap();

        for (int x = 1; x <= map.getColumns(); x++) {
            for (int y = 1; y <= map.getRows(); y++) {
                double distance = zombie.getX() - x;
                if (y == zombie.getY() && distance >= 0 && distance <= 4) {
                    Tile tile = map.getTile(x, y);
                    if (tile != null && tile.getPlant() != null) {
                        tile.getPlant().setCurrentHp(0);
                    }
                }
            }
        }
    }

    public int stealSuns() {
        int sunsToSteal = Math.min(25, GameManagerController.getInstance().getCurrentLevel().getCollectedSunsAmount());
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
