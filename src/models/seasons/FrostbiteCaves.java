package models.seasons;

import enums.PlantTag;
import enums.TileType;
import enums.ZombieEffect;
import models.GameMapRelated.Tile;
import models.Level;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;
import models.zombies.ZombieData;
import models.zombies.ZombieRepository;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class FrostbiteCaves extends Season {
    private final Random random = new Random();
    private final Set<Zombie> movedBySlide = new HashSet<>();

    public FrostbiteCaves(SeasonData data) {
        super(data);
    }

    @Override
    public void applySpecialRules() {
    }

    @Override
    public void initializeGrid() {
    }

    @Override
    public void LevelStarted(Level level) {
        ZombieData defaultZombie = ZombieRepository.getInstance().findById("ZombieDefault");
        for (int y = 1; y <= level.getGameMap().getRows(); y++) {
            for (int x = 1; x <= level.getGameMap().getColumns(); x++) {
                Tile tile = level.getGameMap().getTile(x, y);
                if (tile != null && tile.hasFrozenZombie() && defaultZombie != null) {
                    Zombie zombie = new Zombie(defaultZombie, x, y);
                    zombie.setFrozen(true);
                    level.addActiveZombie(zombie);
                }
            }
        }
    }

    @Override
    public void Update(Level level) {
        for (Zombie zombie : level.getActiveZombies()) {
            zombie.removeEffect(ZombieEffect.FROZEN);
            zombie.removeEffect(ZombieEffect.CHILLED);
            applySlide(level, zombie);
        }
        for (Projectile projectile : level.getActiveProjectiles()) {
            int x = (int) Math.round(projectile.getxCoordinate());
            int y = (int) Math.round(projectile.getyCoordinate());
            Plant plant = level.getPlantAt(x, y);
            if (plant != null && plant.isFullyFrozen()) {
                Plant creator = projectile.getCreatorPlantCategory();
                if (creator != null && creator.hasThisTag(PlantTag.FIRE)) {
                    plant.decreaseIceHP(plant.getIceHP());
                } else {
                    plant.decreaseIceHP(projectile.getDamage());
                }
                projectile.destroy();
            }
        }
        for (Plant plant : level.getActivePlants()) {
            if (plant.isFullyFrozen() && hasNeighboringFirePlant(level, plant)) {
                plant.decreaseIceHP(6);
            }
        }
        for (int y = 1; y <= level.getGameMap().getRows(); y++) {
            for (int x = 1; x <= level.getGameMap().getColumns(); x++) {
                Tile tile = level.getGameMap().getTile(x, y);
                if (tile != null && tile.hasFrozenZombie() && tile.getType() != TileType.ICE) {
                    for (Zombie zombie : level.getActiveZombies()) {
                        if ((int) Math.round(zombie.getX()) == x && zombie.getY() == y) {
                            zombie.setFrozen(false);
                        }
                    }
                    tile.setFrozenZombie(false);
                }
            }
        }
    }

    private void applySlide(Level level, Zombie zombie) {
        Tile tile = level.getGameMap().getTile((int) Math.round(zombie.getX()), zombie.getY());
        if (tile == null || tile.getType() != TileType.SLIDE_UP && tile.getType() != TileType.SLIDE_DOWN) {
            movedBySlide.remove(zombie);
            return;
        }
        if (!movedBySlide.add(zombie) || zombie.getData().getId().equalsIgnoreCase("ZombieIceAgeDodo")) {
            return;
        }
        if (tile.getType() == TileType.SLIDE_UP && zombie.getY() > 1) {
            zombie.setY(zombie.getY() - 1);
        } else if (tile.getType() == TileType.SLIDE_DOWN && zombie.getY() < level.getGameMap().getRows()) {
            zombie.setY(zombie.getY() + 1);
        }
    }

    private boolean hasNeighboringFirePlant(Level level, Plant plant) {
        for (Plant producer : level.getActivePlants()) {
            if (producer.hasThisTag(PlantTag.FIRE) && Math.abs(producer.getX() - plant.getX()) <= 1
                    && Math.abs(producer.getY() - plant.getY()) <= 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void WaveStarted(Level level, int waveNumber) {
        int rowCount = random.nextBoolean() ? 1 : 2;
        Set<Integer> rows = new HashSet<>();
        while (rows.size() < Math.min(rowCount, level.getGameMap().getRows())) {
            rows.add(random.nextInt(level.getGameMap().getRows()) + 1);
        }
        for (Plant plant : level.getActivePlants()) {
            if (rows.contains(plant.getY()) && !plant.hasThisTag(PlantTag.FIRE)) {
                plant.addFreezeLevel(1);
            }
        }
    }

    @Override
    public void PlantPlaced(Level level, Plant plant, int x, int y) {
    }
}
