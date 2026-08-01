package models.seasons;

import enums.TileType;
import models.GameMapRelated.Tile;
import models.Level;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

import java.util.Random;

public class AncientEgypt extends Season {
    public AncientEgypt(SeasonData data) {
        super(data);
    }

    @Override
    public void applySpecialRules() {
        // TODO
    }

    @Override
    public void initializeGrid() {
        // I'm not sure if this is needed?
    }

    @Override
    public void LevelStarted(Level level) {
        int rows = level.getGameMap().getRows();
        int cols = level.getGameMap().getColumns();

        for( int r = 0; r < rows; r++){
            for( int c = 0; c < cols; c++){
                Tile tile = level.getGameMap().getTile(r, c);

                if(tile != null && tile.getType() == TileType.GRAVE){
                    tile.setGrave(true);
                }
            }
        }
    }

    @Override
    public void Update(Level level) {
        for(Projectile projectile : level.getActiveProjectiles()){
            int row = (int) projectile.getyCoordinate();
            int col = (int) projectile.getxCoordinate();

            Tile tile = level.getGameMap().getTile(row, col);

            if(tile != null && tile.isGrave()){
                tile.takeDamage(projectile.getDamage());
                projectile.destroy();
            }
        }

    }

    @Override
    public void WaveStarted(Level level, int waveNumber) {
        if(waveNumber == level.getData().getWaveCount() - 1){
            for(Zombie zombie : level.getActiveZombies()){
                if(checkZombieForSandstorm(zombie)){
                    Random random = new Random();
                    int advanceCol = random.nextInt(4) + 1;
                    int currentCol = (int) zombie.getX();
                    int newCol = currentCol - advanceCol;

                    if (newCol < 1) {
                        newCol = 1;
                    }

                    zombie.setX(newCol);
                }
            }
        }
    }

    private boolean checkZombieForSandstorm(Zombie zombie){
        return zombie.getData().getDisplayName().equalsIgnoreCase("ZombieTutorialDefault") || //TODO: ID, not display name
                zombie.getData().getDisplayName().equalsIgnoreCase("ZombieSandstorm");
    }

    @Override
    public void PlantPlaced(Level level, Plant plant, int x, int y) {
        Tile tile = level.getGameMap().getTile(x, y);
        if(tile != null && tile.isGrave()){
            System.out.println("Cannot place a plant on top of a grave");
        }
        // double check if we should add a plant here or the main manager does it itself?

    }

}

