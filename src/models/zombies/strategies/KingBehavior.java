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
    private static final int ABILITY_TICKS = 50;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
        if (zombie.getAbilityTickTimer() >= ABILITY_TICKS) {
            makeKnight();
            zombie.setAbilityTickTimer(0);
        }
    }

    private void makeKnight() {
        List<Zombie> zombies = new ArrayList<>();
        for (Zombie zombie : GameManagerController.getInstance().getCurrentLevel().getActiveZombies()) {
            if (zombie.getData().getId().equalsIgnoreCase("ZombieDefault") && zombie.getArmors().isEmpty()) {
                zombies.add(zombie);
            }
        }
        if (zombies.isEmpty()) {
            return;
        }
        Collections.shuffle(zombies);
        Zombie target = zombies.get(0);
        target.addArmor(ArmorType.CROWN);
        target.addArmor(ArmorType.SHOULDER_ARMOR);
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        zombie.takeDamage(projectile.getDamage(), projectile.getCreatorPlantCategory());
    }
}
