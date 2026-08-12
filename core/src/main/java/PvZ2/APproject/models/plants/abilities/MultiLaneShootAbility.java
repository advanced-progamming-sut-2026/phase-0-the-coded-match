package PvZ2.APproject.models.plants.abilities;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

public class MultiLaneShootAbility extends ShootAbility {
    public MultiLaneShootAbility() {
        super(1);
    }

    @Override
    public void execute(Plant plant) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return;
        }
        for (int lane = plant.getY() - 1; lane <= plant.getY() + 1; lane++) {
            if (lane >= 1 && hasZombieInLane(level, lane, plant.getX())) {
                level.getActiveProjectiles().add(createProjectile(plant, lane, lane - plant.getY() + 1));
            }
        }
    }
}
