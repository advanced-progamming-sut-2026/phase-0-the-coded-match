package PvZ2.APproject.models.seasons;

import PvZ2.APproject.enums.TileType;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class AncientEgypt extends Season {
    private static final int MAX_GRAVES = 6;
    public AncientEgypt(SeasonData data) {
        super(data);
        this.name = data.getName();
    }

    @Override
    public void initializeGrid() {
        // I'm not sure if this is needed?
    }

    @Override
    public void LevelStarted(Level level) {
        int rows = level.getGameMap().getRows();
        int cols = level.getGameMap().getColumns();
        List<Tile> graves = new ArrayList<>();

        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {
                Tile tile = level.getGameMap().getTile(c, r);
                if (tile != null && tile.getType() == TileType.GRAVE) {
                    tile.setGrave(true, Tile.GraveReward.NONE);
                    graves.add(tile);
                }
            }
        }

        if (graves.size() > MAX_GRAVES) {
            Collections.shuffle(graves);
            for (int i = MAX_GRAVES; i < graves.size(); i++) {
                graves.get(i).setGrave(false, Tile.GraveReward.NONE);
            }
        }
    }

    @Override
    public void Update(Level level, float delta) {
//        for(Projectile projectile : level.getActiveProjectiles()){
//            int row = (int) projectile.getyCoordinate();
//            int col = (int) projectile.getxCoordinate();
//
//            Tile tile = level.getGameMap().getTile(row, col);
//
//            if(tile != null && tile.isGrave()){
//                tile.takeDamage(projectile.getDamage());
//                projectile.destroy();
//            }
//        }

    }

    @Override
    public void WaveStarted(Level level, int waveNumber) {
        if(waveNumber == level.getData().getWaveCount() - 1){
            boolean sandstormOccurred = false;
            for(Zombie zombie : level.getActiveZombies()){
                if(checkZombieForSandstorm(zombie)){
                    Random random = new Random();
                    int advanceCol = random.nextInt(3) + 2;
                    int currentCol = (int) zombie.getX();
                    int newCol = currentCol - advanceCol;

                    if (newCol < 1) {
                        newCol = 1;
                    }

                    zombie.setX(newCol);
                    sandstormOccurred = true;
                }
            }
            if (sandstormOccurred) {
                level.triggerEnvironmentEvent(
                    EnvironmentEvent.EnvironmentEventType.SANDSTORM,
                    1.5f
                );
            }
        }
    }

    private boolean checkZombieForSandstorm(Zombie zombie){
        return zombie.getData().getId().equalsIgnoreCase("ZombieDefault") ||
                zombie.getData().getId().equalsIgnoreCase("ZombieArmor1") ||
                zombie.getData().getId().equalsIgnoreCase("ZombieArmor2");
    }

    @Override
    public void PlantPlaced(Level level, Plant plant, int x, int y) {
        Tile tile = level.getGameMap().getTile(x, y);
        if(tile != null && tile.isGrave()){
            System.out.println("Cannot place a plant on top of a grave");
        }

    }

}

