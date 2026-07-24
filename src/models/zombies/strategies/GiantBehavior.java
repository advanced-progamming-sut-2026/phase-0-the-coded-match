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
        if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.destroyPlant(targetPlant);
            if(targetPlant.isDead()) {
                zombie.setCurrentState(ZombieState.WALKING);
            }
        } else if (zombie.getCurrentState() == ZombieState.WALKING) {
            zombie.walk();
        } else if (zombie.getCurrentHp() <= (zombie.getData().getMaxHP() / 2) && !zombie.isHasThrownImp()) {
            spawnImp(3.0, zombie);
            zombie.setHasThrownImp(true);
        }
    }

    public void spawnImp(double x, Zombie zombie) {
        ZombieData impData = ZombieRepository.getInstance().findByDisplayName("Imp");
        Zombie newImp = new Zombie(impData, x, zombie.getY());
        GameManagerController.getInstance().getCurrentLevel().getActiveZombies().add(newImp);
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
