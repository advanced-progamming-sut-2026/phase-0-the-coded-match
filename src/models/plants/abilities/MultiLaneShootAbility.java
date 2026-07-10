package models.plants.abilities;

import controllers.GameManagerController;
import models.Level;
import models.plants.Plant;

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
            if (lane >= 0 && hasZombieInLane(level, lane, plant.getX())) {
                level.getActiveProjectiles().add(createProjectile(plant, lane, lane - plant.getY() + 1));
            }
        }
    }
}
