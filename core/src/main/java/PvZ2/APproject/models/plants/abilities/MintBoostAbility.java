package PvZ2.APproject.models.plants.abilities;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

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
