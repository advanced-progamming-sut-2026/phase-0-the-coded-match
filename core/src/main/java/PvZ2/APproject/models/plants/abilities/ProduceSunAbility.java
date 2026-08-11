package PvZ2.APproject.models.plants.abilities;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.SunType;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.Sun;
import PvZ2.APproject.models.plants.Plant;

public class ProduceSunAbility implements PlantAbilityHandler {
    private final boolean instant;

    public ProduceSunAbility(boolean instant) {
        this.instant = instant;
    }

    @Override
    public void execute(Plant plant) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return;
        }
        int value = instant ? Math.max(150, plant.getData().getSunCost() * 2) : getProducedSunValue(plant);
        Sun sun = new Sun(plant.getX(), plant.getY(), value, 0, false, SunType.NORMAL);
        level.getActiveSuns().add(sun);
        plant.setProducedSun(true);
        plant.setSunCollected(false);
    }

    private int getProducedSunValue(Plant plant) {
        String text = plant.getData().getBaseAbility();
        if (text == null) {
            return 50;
        }
        if (text.contains("۱۰۰") || text.contains("100")) {
            return 100;
        }
        if (text.contains("۷۵") || text.contains("75")) {
            return 75;
        }
        if (text.contains("۲۵") || text.contains("25")) {
            return 25;
        }
        return 50;
    }
}
