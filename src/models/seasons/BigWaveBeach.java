package models.seasons;

import enums.PlantTag;
import enums.TileType;
import models.GameMapRelated.Tile;
import models.Level;
import models.plants.Plant;
import models.zombies.Zombie;
import models.zombies.ZombieData;
import models.zombies.ZombieRepository;

import java.util.Random;

public class BigWaveBeach extends Season {
    private final Random random = new Random();
    private int currentTideColumn = 6;

    public BigWaveBeach(SeasonData data) {
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
        updateTideBoundary(level, currentTideColumn);
    }

    private void updateTideBoundary(Level level, int tideColumn) {
        for (int y = 1; y <= level.getGameMap().getRows(); y++) {
            for (int x = 1; x <= level.getGameMap().getColumns(); x++) {
                Tile tile = level.getGameMap().getTile(x, y);
                if (tile == null) {
                    continue;
                }
                if (tile.getType() == TileType.NORMAL || tile.getType() == TileType.WATER
                        || tile.getType() == TileType.LOW_TIDE) {
                    tile.setType(x >= tideColumn ? TileType.WATER : TileType.NORMAL);
                }
            }
        }
    }

    @Override
    public void Update(Level level) {
        for (int y = 1; y <= level.getGameMap().getRows(); y++) {
            for (int x = 1; x <= level.getGameMap().getColumns(); x++) {
                Tile tile = level.getGameMap().getTile(x, y);
                if (tile != null && tile.getType() == TileType.WATER) {
                    Plant plant = tile.getPlant();
                    if (plant != null && !plant.hasThisTag(PlantTag.WATER) && tile.getLilyPadPlant() == null) {
                        plant.setCurrentHp(0);
                    }
                }
            }
        }
    }

    @Override
    public void WaveStarted(Level level, int waveNumber) {
        if (waveNumber == 3 || waveNumber == level.getData().getWaveCount()) {
            triggerLowTideEvent(level);
        }
        if (waveNumber > 1) {
            currentTideColumn = Math.max(3, Math.min(level.getGameMap().getColumns(),
                    currentTideColumn + (random.nextBoolean() ? -1 : 1)));
            updateTideBoundary(level, currentTideColumn);
        }
    }

    private void triggerLowTideEvent(Level level) {
        ZombieData data = ZombieRepository.getInstance().findById("ZombieBeachSnorkel");
        if (data == null) {
            return;
        }
        int count = 3 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            int row = random.nextInt(level.getGameMap().getRows()) + 1;
            int width = Math.max(1, level.getGameMap().getColumns() - currentTideColumn + 1);
            int column = currentTideColumn + random.nextInt(width);
            level.addActiveZombie(new Zombie(data, column, row));
        }
    }

    @Override
    public void PlantPlaced(Level level, Plant plant, int x, int y) {
        Tile tile = level.getGameMap().getTile(x, y);
        if (tile != null && tile.getType() == TileType.WATER
                && normalize(plant.getData().getName()).equals("lilypad")) {
            tile.setLilyPadPlant(plant);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.replace(" ", "").replace("-", "").toLowerCase();
    }
}
