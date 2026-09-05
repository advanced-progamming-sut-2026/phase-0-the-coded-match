package PvZ2.APproject.models.seasons;

import PvZ2.APproject.enums.PlantTag;
import PvZ2.APproject.enums.TileType;
import PvZ2.APproject.models.GameMapRelated.GameMap;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class FrostbiteCaves extends Season {
    private int slipperyTileCount = 8;

    public FrostbiteCaves(SeasonData data) {
        super(data);
        this.name = data.getName();
    }

    @Override
    public void initializeGrid() {
        // i dont think we need this as well
    }

    @Override
    public void levelStarted(Level level) {
        initializeSlipperyTiles(level);
        for(Zombie zombie : level.getActiveZombies()){
            if(zombieShouldBeFrozen(zombie)){
                zombie.setFrozenInBlock(true);
                zombie.setBlockIceHP(60);
            }
        }
    }

    private void initializeSlipperyTiles(Level level) {
        GameMap map = level.getGameMap();
        List<Tile> availableTiles = new ArrayList<>();

        for (int row = 1; row <= map.getRows(); row++) {
            for (int col = 1; col <= map.getColumns(); col++) {

                Tile tile = map.getTile(col, row);

                if (tile != null && tile.getType() == TileType.NORMAL) {
                    availableTiles.add(tile);
                }
            }
        }

        Collections.shuffle(availableTiles);

        int tilesToMakeSlippery = Math.min(slipperyTileCount, availableTiles.size());
        for (int i = 0; i < tilesToMakeSlippery; i++) {
            availableTiles.get(i).setSlippery(true);
        }
    }

    private boolean zombieShouldBeFrozen(Zombie zombie){
        if (zombie == null || zombie.getData() == null) return false;
        return zombie.getData().getId().toLowerCase().contains("iceage");
    }

    @Override
    public void update(Level level, float delta) {

        for(Projectile projectile : level.getActiveProjectiles()){
            int x = (int) projectile.getxCoordinate();
            int y = (int) projectile.getyCoordinate();

            Plant plant = level.getPlantAt(x, y);
            if(plant != null && plant.isFullyFrozen()){
                if(projectile.getCreatorPlantCategory() != null && projectile.getCreatorPlantCategory().
                    hasThisTag(PlantTag.FIRE)){
                    plant.decreaseIceHP(plant.getIceHP());
                }else{
                    plant.decreaseIceHP(projectile.getDamage());
                }

                projectile.destroy();
            }
        }

        for(Plant plant : level.getActivePlants()){
            if(plant.isFullyFrozen()){
                if(hasNeighboringFirePlant(level, plant)){
                    plant.decreaseIceHP((int) (6*delta));
                }
            }
        }
    }

    private boolean hasNeighboringFirePlant(Level level, Plant plant){
        for(Plant producer : level.getActivePlants()){
            if (producer.hasThisTag(PlantTag.FIRE)) {
                int disX = Math.abs(producer.getX() - plant.getX());
                int disY = Math.abs(producer.getY() - plant.getY());
                if(disX <= 1 && disY <= 1){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void waveStarted(Level level, int waveNumber) {
        for (Zombie zombie : level.getActiveZombies()) {
            if (zombieShouldBeFrozen(zombie) && !zombie.isFrozenInBlock()) {
                zombie.setFrozenInBlock(true);
                zombie.setBlockIceHP(60);
            }
        }
        Random random = new Random();
        int randomRow = 1 + random.nextInt(level.getGameMap().getRows());
        int affectedRows = random.nextInt(2) + 1;
        for (int i = 0; i < affectedRows; i++) {
            for (Plant plant : level.getActivePlants()) {
                if (plant.getY() == randomRow && !plant.hasThisTag(PlantTag.FIRE)) {
                    plant.addFreezeLevel(1);
                }
            }
        }
        level.triggerEnvironmentEvent(
            EnvironmentEvent.EnvironmentEventType.ICE_WIND,
            1.5f
//           ,affectedRows
        );

    }

    @Override
    public void plantPlaced(Level level, Plant plant, int x, int y) {
        //check with game manager; may switch planting plants here later on
    }
}



