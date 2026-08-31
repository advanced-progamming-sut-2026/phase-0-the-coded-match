package PvZ2.APproject.models.seasons;

import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.ZombieData;
import PvZ2.APproject.models.zombies.ZombieRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class DarkAges extends Season {
    private static final int MAX_GRAVES = 7;
    public DarkAges(SeasonData data) {
        super(data);
        this.name = data.getName();
    }



    @Override
    public void initializeGrid() {
        // i think i should erase these
    }

    @Override
    public void LevelStarted(Level level) {

        level.setSkySunProducer(null);
        markNecromancyTiles(level);
    }

    private void markNecromancyTiles(Level level){
        Random random = new Random();

        int rows = level.getGameMap().getRows();
        int cols = level.getGameMap().getColumns();

        int necromancyTileCount = Math.min(rows * cols, 2 + random.nextInt(3));

        for (int i = 0; i < necromancyTileCount; i++) {
            int row = 1 + random.nextInt(Math.max(1, rows));
            int col = 1 + random.nextInt(Math.max(1, cols));

            Tile tile = level.getGameMap().getTile(col, row);

            if (tile != null && !tile.isGrave()) {
                tile.setNecromancyPotential(true);
            }
        }
    }


    @Override
    public void Update(Level level, float delta) {return;}

    @Override
    public void WaveStarted(Level level, int waveNumber) {
        triggerNecromancy(level);
        spawnRandomWaveGraves(level);
    }

    private void triggerNecromancy(Level level){
        Random random = new Random();
        int rows = level.getGameMap().getRows();
        int cols = level.getGameMap().getColumns();
        List<ZombieData> necromancyZombies = new ArrayList<>(this.getAllowedZombies());
        if (necromancyZombies.isEmpty()) return;

        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {
                Tile tile = level.getGameMap().getTile(c, r);

                if (tile != null && tile.holdsNecromancyPotential() && tile.isGrave()) {
                    String chosenZombie = necromancyZombies.get(random.nextInt(necromancyZombies.size())).getId();
                    Zombie zombie = new Zombie(ZombieRepository.getInstance().findById(chosenZombie), c, r);
                    level.getActiveZombies().add(zombie);
                    tile.setNecromancyPotential(false);
                }
            }
        }
    }

    private void spawnRandomWaveGraves(Level level){
        if (countGraves(level) >= MAX_GRAVES) return;
        Random random = new Random();
        int rows = level.getGameMap().getRows();
        int cols = level.getGameMap().getColumns();
        int minCol = cols >= 3 ? 2 : 1;
        int maxCol = Math.max(minCol, cols - 1);

        for (int attempt = 0; attempt < 20; attempt++) {
            int randomRow = 1 + random.nextInt(Math.max(1, rows));
            int randomCol = minCol + random.nextInt(Math.max(1, maxCol - minCol + 1));
            Tile tile = level.getGameMap().getTile(randomCol, randomRow);
            if (tile != null && tile.getPlant() == null && !tile.isGrave()) {
                Tile.GraveReward reward = rollRandomReward();
                tile.setGrave(true, reward);
                tile.setNecromancyPotential(true);
                System.out.println("ALERT: A new grave (" + reward + ") has spawned at position ["
                        + randomCol + ", " + randomRow + "]!");
                return;
            }
        }
    }

    private int countGraves(Level level) {
        int count = 0;
        int rows = level.getGameMap().getRows();
        int cols = level.getGameMap().getColumns();
        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {
                Tile tile = level.getGameMap().getTile(c, r);
                if (tile != null && tile.isGrave()) count++;
            }
        }
        return count;
    }

    private Tile.GraveReward rollRandomReward(){
        Random random = new Random();
        int roll = random.nextInt(100);
        if (roll < 20) {
            return Tile.GraveReward.SUN_50;
        } else if (roll < 35) {
            return Tile.GraveReward.PLANT_FOOD;
        } else {
            return Tile.GraveReward.NONE;
        }
    }

    @Override
    public void PlantPlaced(Level level, Plant plant, int x, int y) {

    }


}

