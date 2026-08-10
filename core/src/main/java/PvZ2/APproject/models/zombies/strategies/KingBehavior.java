package models.zombies.strategies;

import controllers.GameManagerController;
import enums.ArmorType;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KingBehavior implements ZombieBehavior {
    private int abilityTimer = 3;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
        if (zombie.getAbilityTickTimer() == abilityTimer) {
            makeKnight();
            zombie.setAbilityTickTimer(0);
        }
    }

    public void makeKnight() {
        List<Zombie> defaultZombies = getDefaultZombies();

        if (defaultZombies.isEmpty()) {
            return;
        }
        Collections.shuffle(defaultZombies);

        for (int i = 0; i < 1; i++) {
            Zombie target = defaultZombies.get(i);
            target.addArmor(ArmorType.SHOULDER_ARMOR);
            target.addArmor(ArmorType.CROWN);
        }
    }

    public List<Zombie> getDefaultZombies() {
        List<Zombie> zombies = new ArrayList<>();
        for (Zombie zombie : GameManagerController.getInstance().getCurrentLevel().getActiveZombies()) {
            if (zombie.getData().getId().equalsIgnoreCase("ZombieDefault")) {
                zombies.add(zombie);
            }
        }
        return zombies;
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
