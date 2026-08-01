package models.zombies.strategies;

import controllers.GameManagerController;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WizardBehavior implements ZombieBehavior {
    private static final int SPELL_TICKS = 50;
    private final List<Plant> transformedPlants = new ArrayList<>();

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
        if (zombie.getAbilityTickTimer() >= SPELL_TICKS) {
            transformRandomPlant();
            zombie.setAbilityTickTimer(0);
        }
        if (targetPlant != null) {
            transform(targetPlant);
        } else {
            zombie.walk();
        }
    }

    private void transformRandomPlant() {
        List<Plant> plants = new ArrayList<>(GameManagerController.getInstance().getCurrentLevel().getActivePlants());
        plants.removeAll(transformedPlants);
        if (!plants.isEmpty()) {
            Collections.shuffle(plants);
            transform(plants.get(0));
        }
    }

    private void transform(Plant plant) {
        if (plant != null && !transformedPlants.contains(plant)) {
            plant.setDisabled(true);
            plant.setProtectedFromZombies(true);
            transformedPlants.add(plant);
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }

    @Override
    public void onDeath(Zombie zombie) {
        for (Plant plant : transformedPlants) {
            plant.setDisabled(false);
            plant.setProtectedFromZombies(false);
        }
        transformedPlants.clear();
    }
}
