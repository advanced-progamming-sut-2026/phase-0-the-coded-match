package models.zombies.strategies;

import controllers.GameManagerController;
import enums.PlantCategory;
import enums.PlantTag;
import enums.TileType;
import enums.ZombieState;
import models.Level;
import models.Projectile;
import models.Sun;
import models.GameMapRelated.GameMap;
import models.GameMapRelated.Tile;
import models.plants.Plant;
import models.zombies.Zombie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NormalBehavior implements ZombieBehavior {
    private static final int ABILITY_INTERVAL = 60;

    @Override
    public void updateZombie(Zombie zombie, Plant targetPlant) {
        if (zombie.getData().getId().equalsIgnoreCase("ZombieArmZombieNewspaper") && zombie.getArmors().isEmpty()) {
            zombie.setCurrentState(ZombieState.RUNNING);
        }
        if (zombie.getData().getId().equalsIgnoreCase("ZombieRa")) {
            stealDroppedSuns(zombie);
        }
        if (zombie.getData().getId().equalsIgnoreCase("ZombieTombRaiser")) {
            zombie.setAbilityTickTimer(zombie.getAbilityTickTimer() + 1);
            if (zombie.getAbilityTickTimer() >= ABILITY_INTERVAL) {
                raiseTomb();
                zombie.setAbilityTickTimer(0);
            }
        }
        if (zombie.getCurrentState() == ZombieState.EATING) {
            zombie.attack(targetPlant);
            if (targetPlant == null || targetPlant.isDead()) {
                zombie.setCurrentState(ZombieState.WALKING);
            }
        } else if (zombie.getCurrentState() == ZombieState.RUNNING) {
            zombie.run();
        } else {
            zombie.walk();
        }
    }

    private void raiseTomb() {
        List<Tile> emptyTiles = getValidTilesForGrave();
        Collections.shuffle(emptyTiles);
        for (int i = 0; i < Math.min(2, emptyTiles.size()); i++) {
            emptyTiles.get(i).setGrave(true, Tile.GraveReward.NONE);
        }
    }

    private List<Tile> getValidTilesForGrave() {
        List<Tile> validTiles = new ArrayList<>();
        GameMap map = GameManagerController.getInstance().getCurrentLevel().getGameMap();
        for (int y = 1; y <= map.getRows(); y++) {
            for (int x = 1; x <= map.getColumns(); x++) {
                Tile tile = map.getTile(x, y);
                if (tile != null && tile.getType() == TileType.NORMAL && tile.isEmpty()) {
                    validTiles.add(tile);
                }
            }
        }
        return validTiles;
    }

    private void stealDroppedSuns(Zombie zombie) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        List<Sun> stolen = new ArrayList<>(level.getActiveSuns());
        level.getActiveSuns().clear();
        zombie.getStolenActiveSuns().addAll(stolen);
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        Plant creator = projectile.getCreatorPlantCategory();
        if (zombie.getData().getId().equalsIgnoreCase("ZombieLostCityJane") && creator != null
                && creator.getData().getCategory() == PlantCategory.LOBBER) {
            projectile.destroy();
            return;
        }
        if (zombie.getData().getId().equalsIgnoreCase("ZombieDarkImpDragon") && creator != null
                && creator.hasThisTag(PlantTag.FIRE)) {
            projectile.destroy();
            return;
        }
        zombie.takeDamage(projectile.getDamage(), creator);
    }
}
