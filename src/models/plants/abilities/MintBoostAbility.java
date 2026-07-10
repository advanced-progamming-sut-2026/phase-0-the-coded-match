package models.plants.abilities;

import controllers.GameManagerController;
import models.Level;
import models.plants.Plant;

public class MintBoostAbility implements PlantAbilityHandler {
    @Override
    public void execute(Plant plant) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return;
        }
        for (Plant activePlant : level.getActivePlants()) {
            if (activePlant != plant && activePlant.getData().getCategory() == plant.getData().getCategory()) {
                activePlant.activatePlantFood();
            }
        }
        plant.setCurrentHp(0);
    }
}
