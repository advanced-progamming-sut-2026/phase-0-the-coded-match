package models.zombies.strategies;

import controllers.GameManagerController;
import enums.ZombieState;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;
import models.zombies.ZombieData;
import models.zombies.ZombieRepository;

public class GiantBehavior implements ZombieBehavior {
    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        if (zombie.getCurrentHp() <= zombie.getMaxHp() / 2 && !zombie.isHasThrownImp()) {
            spawnImp(zombie);
            zombie.setHasThrownImp(true);
        }
        if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.destroyPlant(targetPlant);
            zombie.setCurrentState(ZombieState.WALKING);
        } else {
            zombie.walk();
        }
    }

    private void spawnImp(Zombie zombie) {
        ZombieData impData = ZombieRepository.getInstance().findById("ZombieImp");
        if (impData != null) {
            Zombie imp = new Zombie(impData, 3, zombie.getY());
            GameManagerController.getInstance().getCurrentLevel().getActiveZombies().add(imp);
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
