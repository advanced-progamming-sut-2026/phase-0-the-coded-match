package models.seasons;

import enums.TileType;
import models.GameMapRelated.Tile;
import models.Level;
import models.plants.Plant;
import models.zombies.Zombie;
import models.zombies.ZombieData;
import models.zombies.ZombieRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DarkAges extends Season {
    private final Random random = new Random();

    public DarkAges(SeasonData data) {
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
        level.setSkySunProducer(null);
        int count = Math.min(3, level.getGameMap().getRows());
        for (int i = 0; i < count; i++) {
            int x = 2 + random.nextInt(Math.max(1, level.getGameMap().getColumns() - 2));
            int y = random.nextInt(level.getGameMap().getRows()) + 1;
            Tile tile = level.getGameMap().getTile(x, y);
            if (tile != null) {
                tile.setHoldsNecromancyPotential(true);
            }
        }
    }

    @Override
    public void Update(Level level) {
    }

    @Override
    public void WaveStarted(Level level, int waveNumber) {
        triggerNecromancy(level);
        spawnRandomWaveGraves(level);
    }

    private void triggerNecromancy(Level level) {
        List<String> zombies = new ArrayList<>(level.getData().getAllowedZombies());
        if (zombies.isEmpty()) {
            return;
        }
        for (int y = 1; y <= level.getGameMap().getRows(); y++) {
            for (int x = 1; x <= level.getGameMap().getColumns(); x++) {
                Tile tile = level.getGameMap().getTile(x, y);
                if (tile == null || !tile.holdsNecromancyPotential() || !tile.isGrave()) {
                    continue;
                }
                ZombieData data = ZombieRepository.getInstance().findById(zombies.get(random.nextInt(zombies.size())));
                if (data != null) {
                    level.addActiveZombie(new Zombie(data, x, y));
                }
            }
        }
    }

    private void spawnRandomWaveGraves(Level level) {
        int graveCount = 1 + random.nextInt(2);
        for (int i = 0; i < graveCount; i++) {
            int x = 2 + random.nextInt(Math.max(1, level.getGameMap().getColumns() - 2));
            int y = random.nextInt(level.getGameMap().getRows()) + 1;
            Tile tile = level.getGameMap().getTile(x, y);
            if (tile != null && tile.getPlant() == null && !tile.isGrave()
                    && tile.getType() != TileType.WATER) {
                Tile.GraveReward reward = rollRandomReward();
                tile.setGrave(true, reward);
                if (random.nextBoolean()) {
                    tile.setHoldsNecromancyPotential(true);
                }
                System.out.println("A new grave (" + reward + ") appeared at (" + x + ", " + y + ").");
            }
        }
    }

    private Tile.GraveReward rollRandomReward() {
        int roll = random.nextInt(100);
        if (roll < 20) {
            return Tile.GraveReward.SUN_50;
        }
        if (roll < 35) {
            return Tile.GraveReward.PLANT_FOOD;
        }
        return Tile.GraveReward.NONE;
    }

    @Override
    public void PlantPlaced(Level level, Plant plant, int x, int y) {
    }
}
