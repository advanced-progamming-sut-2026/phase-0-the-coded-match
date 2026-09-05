package PvZ2.APproject.models.plants.abilities;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.PlantState;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;

public class MultiLaneShootAbility extends ShootAbility {
    public MultiLaneShootAbility() {
        super(1);
    }

    @Override
    public void execute(Plant plant) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null || level.getGameMap() == null) {
            return;
        }

        int minLane = Math.max(1, plant.getY() - 1);
        int maxLane = Math.min(level.getGameMap().getRows(), plant.getY() + 1);
        boolean fired = false;

        for (int lane = minLane; lane <= maxLane; lane++) {
            if (hasZombieInLane(level, lane, plant.getX())) {
                level.getActiveProjectiles().add(createProjectile(plant, lane, lane - plant.getY() + 1));
                fired = true;
            }
        }

        if (fired) {
            plant.setState(PlantState.SHOOTING);
        }
    }
}
