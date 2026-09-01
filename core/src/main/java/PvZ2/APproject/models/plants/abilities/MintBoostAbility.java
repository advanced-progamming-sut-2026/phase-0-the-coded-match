package PvZ2.APproject.models.plants.abilities;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

public class MintBoostAbility implements PlantAbilityHandler {
    private boolean executing;

    @Override
    public void execute(Plant plant) {
        if (executing) return;
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) return;
        executing = true;
        try {
            for (Plant activePlant : new java.util.ArrayList<>(level.getActivePlants())) {
                if (activePlant != plant && !activePlant.isDead() &&
                    activePlant.getData().getCategory() == plant.getData().getCategory()) {
                    activePlant.activatePlantFood();
                }
            }
        } finally {
            executing = false;
        }
        plant.setCurrentHp(0);
    }
}
