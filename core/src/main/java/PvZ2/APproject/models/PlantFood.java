package PvZ2.APproject.models;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.SunType;

public class PlantFood extends Sun {
    public PlantFood(double x, double y) {
        super((float) x, (float) y, 0, 0f, false, SunType.NORMAL);
    }

    @Override
    public void collect() {
        Level currentLevel = GameManagerController.getInstance().getCurrentLevel();
        if (currentLevel == null) return;
        if (currentLevel.getPlantFoodCount() < 4) {
            currentLevel.setPlantFoodCount(currentLevel.getPlantFoodCount() + 1);
        }
        currentLevel.getActiveSuns().remove(this);
    }
}
