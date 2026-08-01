package models.zombies.strategies;

import controllers.GameManagerController;
import enums.ZombieState;
import models.GameMapRelated.Tile;
import models.Level;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

public class TurquoiseBehavior implements ZombieBehavior {
    private static final int CAST_TICKS = 50;
    private boolean casting;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (!casting && level.isPlantWithinDistance(zombie, 4)) {
            casting = true;
            zombie.setCurrentState(ZombieState.STEALING);
            zombie.setAbilityTickTimer(0);
        }
        if (!casting) {
            zombie.walk();
            return;
        }
        int timer = zombie.getAbilityTickTimer() + 1;
        zombie.setAbilityTickTimer(timer);
        if (timer % 10 == 0) {
            int stolen = Math.min(25, level.getCollectedSunsAmount());
            level.setCollectedSunsAmount(level.getCollectedSunsAmount() - stolen);
            zombie.setStolenSuns(zombie.getStolenSuns() + stolen);
        }
        if (timer >= CAST_TICKS) {
            laser(zombie);
            zombie.setAbilityTickTimer(0);
            zombie.setCurrentState(ZombieState.WALKING);
            casting = false;
        }
    }

    private void laser(Zombie zombie) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        int startX = (int) Math.floor(zombie.getX()) - 1;
        int endX = Math.max(1, startX - 3);
        for (int x = startX; x >= endX; x--) {
            Tile tile = level.getGameMap().getTile(x, zombie.getY());
            if (tile != null && tile.getPlant() != null) {
                tile.getPlant().setCurrentHp(0);
            }
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
