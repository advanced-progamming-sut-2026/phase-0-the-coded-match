package models.seasons;

import enums.TileType;
import models.GameMapRelated.Tile;
import models.Level;
import models.plants.Plant;
import models.zombies.Zombie;

import java.util.Random;

public class AncientEgypt extends Season {
    private final Random random = new Random();

    public AncientEgypt(SeasonData data) {
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
        for (int y = 1; y <= level.getGameMap().getRows(); y++) {
            for (int x = 1; x <= level.getGameMap().getColumns(); x++) {
                Tile tile = level.getGameMap().getTile(x, y);
                if (tile != null && tile.getType() == TileType.GRAVE) {
                    tile.setGrave(true);
                }
            }
        }
    }

    @Override
    public void Update(Level level) {
    }

    @Override
    public void WaveStarted(Level level, int waveNumber) {
        if (waveNumber != level.getData().getWaveCount()) {
            return;
        }
        for (Zombie zombie : level.getActiveZombies()) {
            if (canUseSandstorm(zombie)) {
                zombie.setX(Math.max(1, zombie.getX() - random.nextInt(4) - 1));
            }
        }
    }

    private boolean canUseSandstorm(Zombie zombie) {
        String id = zombie.getData().getId();
        return id.equalsIgnoreCase("ZombieDefault") || id.equalsIgnoreCase("ZombieArmor1")
                || id.equalsIgnoreCase("ZombieRa");
    }

    @Override
    public void PlantPlaced(Level level, Plant plant, int x, int y) {
    }
}
