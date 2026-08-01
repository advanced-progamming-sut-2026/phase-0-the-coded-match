package models.seasons;

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
    public DarkAges(SeasonData data) {
        super(data);
        this.name = data.getName();
        this.setUnlocked(true);
    }



    @Override
    public void initializeGrid() {
        // i think i should erase these
    }

    @Override
    public void LevelStarted(Level level) {
        level.setSkySunProducer(null);
    }


    @Override
    public void Update(Level level) {return;}

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

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile tile = level.getGameMap().getTile(c, r);

                if (tile != null && tile.holdsNecromancyPotential() && tile.isGrave()) {
                    String chosenZombie = necromancyZombies.get(random.nextInt(necromancyZombies.size())).getId();
                    Zombie zombie = new Zombie(ZombieRepository.getInstance().findById(chosenZombie), c, r);
                    if(zombie != null) {
                        level.getActiveZombies().add(zombie);
                    }
                }
            }
        }
    }

    private void spawnRandomWaveGraves(Level level){
        Random random = new Random();
        int rows = level.getGameMap().getRows();
        int cols = level.getGameMap().getColumns();

        int graveCount = 1 + random.nextInt(2);
        for (int i = 0; i < graveCount; i++) {
            int randomRow = random.nextInt(rows);
            int randomCol = 2 + random.nextInt(cols - 3); // middle right cols

            Tile tile = level.getGameMap().getTile(randomCol, randomRow);
            if (tile != null && tile.getPlant() == null && !tile.isGrave()) {
                Tile.GraveReward reward = rollRandomReward();
                tile.setGrave(true, reward);

                // DISPLAY MESSAGE
                System.out.println("ALERT: A new grave (" + reward + ") has spawned at position ["
                        + randomCol + ", " + randomRow + "]!");
            }
        }
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

