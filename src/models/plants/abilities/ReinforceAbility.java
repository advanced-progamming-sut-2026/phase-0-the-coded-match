package models.plants.abilities;

import models.plants.Plant;

public class ReinforceAbility implements PlantAbilityHandler {
    @Override
    public void execute(Plant plant) {
        plant.setCurrentHp(plant.getCurrentHp() + plant.getData().getBaseHp());
    }
}
