package PvZ2.APproject.models.zombies.strategies;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.PlantCategory;
import PvZ2.APproject.enums.PlantTag;
import PvZ2.APproject.enums.TileType;
import PvZ2.APproject.enums.ZombieState;
import PvZ2.APproject.models.GameMapRelated.GameMap;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.Sun;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NormalBehavior implements ZombieBehavior {
    private int abilityTimer = 60;
    private static final int TOMB_RAISER_INTERVAL = 100;
    private static final int MAX_GRAVES = 6;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        if (zombie.getData().getId().equalsIgnoreCase("ZombieArmZombieNewspaper")) {
            if (zombie.getArmors().isEmpty()) {
                zombie.setCurrentState(ZombieState.RUNNING);
            }
        } else if (zombie.getData().getId().equalsIgnoreCase("ZombieRa")) {
            stealDroppedSuns(zombie);
        } else if (zombie.getData().getId().equalsIgnoreCase("ZombieTombRaiser")) {
            zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
            if (zombie.getAbilityTickTimer() >= TOMB_RAISER_INTERVAL) {
                raiseTomb();
                zombie.setAbilityTickTimer(0);
            }
        } else if ((zombie.getData().getId().equalsIgnoreCase("ZombieIceAgeHunter") ||
                zombie.getData().getId().equalsIgnoreCase("ZombieBeachOctopus")) && targetPlant != null) {
            zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
            if (zombie.getAbilityTickTimer() >= abilityTimer) {
                shootProjectile(zombie);
                zombie.setAbilityTickTimer(0);
            }
        }

        if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.attack(targetPlant);
            if(targetPlant.isDead()) {
                zombie.setCurrentState(ZombieState.WALKING);
            }
        } else if (zombie.getCurrentState() == ZombieState.WALKING) {
            zombie.walk();
        } else if (zombie.getCurrentState() == ZombieState.RUNNING) {
            zombie.run();
        }
    }

    public void raiseTomb() {
        GameMap map = GameManagerController.getInstance().getCurrentLevel().getGameMap();
        if (countGraves(map) >= MAX_GRAVES) return;
        List<Tile> emptyTiles = getValidTilesForGrave();
        if (emptyTiles.isEmpty()) return;
        Collections.shuffle(emptyTiles);
        emptyTiles.get(0).setGrave(true, Tile.GraveReward.NONE);
    }

    private int countGraves(GameMap map) {
        int count = 0;
        for (int row = 1; row <= map.getRows(); row++) {
            for (int col = 1; col <= map.getColumns(); col++) {
                Tile tile = map.getTile(col, row);
                if (tile != null && tile.isGrave()) count++;
            }
        }
        return count;
    }

    public List<Tile> getValidTilesForGrave() {
        List<Tile> validTiles = new ArrayList<>();
        GameMap map = GameManagerController.getInstance().getCurrentLevel().getGameMap();
        for (int row = 1; row <= map.getRows(); row++) {
            for (int col = 1; col <= map.getColumns(); col++) {
                Tile tile = map.getTile(col, row);

                if (tile == null) {
                    continue;
                }

                if (tile.getType() == TileType.NORMAL && tile.isEmpty()) {
                    validTiles.add(tile);
                }
            }
        }
        return validTiles;
    }

    public void stealDroppedSuns(Zombie zombie) {
        List<Sun> activeSuns = GameManagerController.getInstance().getCurrentLevel().getActiveSuns();
        if (zombie.getAbilityTickTimer() == abilityTimer) {
            if (activeSuns != null && !activeSuns.isEmpty()) {
                Random random = new Random();
                int randomIndex = random.nextInt(activeSuns.size());
                Sun stolenSun = activeSuns.get(randomIndex);
                removeSun(stolenSun);
                zombie.getStolenActiveSuns().add(stolenSun);
            }
            zombie.setAbilityTickTimer(0);
        } else {
            zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
        }
    }

    public void removeSun(Sun sun) {
        GameManagerController.getInstance().getCurrentLevel().getActiveSuns().remove(sun);
    }

    public void shootProjectile(Zombie zombie) {
        double projectileSpeed = Math.max(0.2, zombie.getData().getSpeed() / 20.0);
        Projectile icyProjectile = new Projectile(zombie.getX(), zombie.getY(), projectileSpeed,
                zombie.getData().getEatDPS(), true, false, null);
        GameManagerController.getInstance().getCurrentLevel().getActiveProjectiles().add(icyProjectile);
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        String zombieId = zombie.getData().getId();
        Plant creator = projectile.getCreatorPlantCategory();
        if (creator == null) return;
        if (zombieId.equalsIgnoreCase("ZombieLostCityJane")) {
            if (creator.getData().getCategory() == PlantCategory.LOBBER) {
                return;
            }
        }
        if (zombieId.equalsIgnoreCase("ZombieDarkImpDragon")) {
            if (creator.hasThisTag(PlantTag.FIRE)) {
                return;
            }
        }
        zombie.takeDamage(projectile.getDamage(), creator);
    }
}
