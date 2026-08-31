package PvZ2.APproject.models.zombies.strategies;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.ZombieState;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.ZombieData;
import PvZ2.APproject.models.zombies.ZombieRepository;

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
        }
        if (zombie.getCurrentHp() <= (zombie.getMaxHp() / 2) && !zombie.isHasThrownImp()) {
            spawnImp(3.0, zombie);
            zombie.setHasThrownImp(true);
        }
    }

    public void spawnImp(double x, Zombie zombie) {
        ZombieData impData = ZombieRepository.getInstance().findByDisplayName("Imp");
        if (impData == null) impData = ZombieRepository.getInstance().findById("ZombieImp");
        if (impData == null || GameManagerController.getInstance().getCurrentLevel() == null) return;
        Zombie newImp = new Zombie(impData, x, zombie.getY());
        GameManagerController.getInstance().getCurrentLevel().getActiveZombies().add(newImp);
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
