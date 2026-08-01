package models.seasons;

import enums.PlantTag;
import enums.TileType;
import enums.ZombieState;
import models.GameMapRelated.Tile;
import models.Level;
import models.plants.Plant;
import models.zombies.Zombie;
import models.zombies.ZombieData;
import models.zombies.ZombieRepository;

import java.util.List;
import java.util.Random;

public class BigWaveBeach extends Season {

    private int currentTideColumn = 5;

    public BigWaveBeach(SeasonData data) {
        super(data);
        this.name = data.getName();
        this.setUnlocked(true);
    }

    @Override
    public void initializeGrid() {

    }

    @Override
    public void LevelStarted(Level level) {
        updateTideBoundary(level, currentTideColumn);
    }

    private void updateTideBoundary(Level level, int tideColumn){
        int rows = level.getGameMap().getRows();
        int cols = level.getGameMap().getColumns();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile tile = level.getGameMap().getTile(r, c);
                if (tile != null) {
                    if(c >= tideColumn){
                        tile.setType(TileType.WATER);
                    }else {
                        if (tile.getType() == TileType.WATER) {
                            tile.setType(TileType.NORMAL);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void Update(Level level) {
        int rows = level.getGameMap().getRows();
        int cols = level.getGameMap().getColumns();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile tile = level.getGameMap().getTile(r, c);

                if (tile != null && tile.getType()==TileType.WATER) {
                    Plant plant = tile.getPlant();

                    // If water covers a plant that isn't aquatic and has no Lily Pad support, wash it away
                    if (plant != null && !plant.hasThisTag(PlantTag.WATER) && tile.getLilyPadPlant() == null) {
                        level.getActivePlants().remove(plant);
                        tile.setPlant(null);
                    }
                }
            }
        }

        for (Zombie zombie : level.getActiveZombies()) {
            if (zombie.getData().getId().contains("Beach")) {
                int zombieCol = (int) zombie.getX();
                int zombieRow = (int) zombie.getY();

                Tile tile = level.getGameMap().getTile(zombieCol, zombieRow);
                if (tile != null && tile.getType() == TileType.WATER && zombie.getCurrentState() != ZombieState.EATING) {
                    zombie.setSubmerged(true);
                } else {
                    zombie.setSubmerged(false);
                }
            }
        }
    }

    @Override
    public void WaveStarted(Level level, int waveNumber) {
        Random random = new Random();
        if (waveNumber == 2 || waveNumber == level.getData().getWaveCount() - 1) {
            triggerLowTideEvent(level);
        }

        // Shift tide boundary by 1 column (left or right)
        if (waveNumber > 0) {
            int tideShift = random.nextBoolean() ? -1 : 1;
            currentTideColumn = Math.max(3, Math.min(7, currentTideColumn + tideShift));
            updateTideBoundary(level, currentTideColumn);
        }
    }

    private void triggerLowTideEvent(Level level){
        Random random = new Random();
        int extraZombies = 3 + random.nextInt(3); // Spawn 3 to 5 zombies

        for (int i = 0; i < extraZombies; i++) {
            int randomRow = random.nextInt(level.getGameMap().getRows());

            int randomCol = currentTideColumn + random.nextInt(9 - currentTideColumn);
            int randomZombie = random.nextInt(this.getAllowedZombies().size());
            String name = this.getAllowedZombies().get(randomZombie).getId();
            Zombie lowTideZombie = new Zombie (ZombieRepository.getInstance().findById(name), randomCol, randomRow);
            level.getActiveZombies().add(lowTideZombie);
        }
    }

    @Override
    public void PlantPlaced(Level level, Plant plant, int x, int y) {
        Tile tile = level.getGameMap().getTile(x, y);
        if (tile == null) return;

        if (plant.getData().getName().equalsIgnoreCase("LilyPad") && tile.getType() == TileType.WATER) {
            tile.setLilyPadPlant(plant);
        }
    }

}
