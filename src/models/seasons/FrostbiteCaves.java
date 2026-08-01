package models.seasons;

import enums.PlantTag;
import models.Level;
import models.Projectile;
import models.plants.Plant;
import models.zombies.Zombie;

import java.util.Random;

public class FrostbiteCaves extends Season {

    public FrostbiteCaves(SeasonData data) {
        super(data);
        this.name = data.getName();
        this.setUnlocked(true);
    }

    @Override
    public void applySpecialRules() {
        // TODO i dont think we need this method
    }

    @Override
    public void initializeGrid() {
        // i dont think we need this as well
    }

    @Override
    public void LevelStarted(Level level) {
        for(Zombie zombie : level.getActiveZombies()){
            if(zombieShouldBeFrozen(zombie)){
                zombie.setFrozen(true);
            }
        }
    }

    private boolean zombieShouldBeFrozen(Zombie zombie){
        if (zombie == null || zombie.getData() == null) return false;
        return zombie.getData().getId().toLowerCase().contains("IceAge");
    }

    @Override
    public void Update(Level level) {

        for(Projectile projectile : level.getActiveProjectiles()){
            int x = (int) projectile.getxCoordinate();
            int y = (int) projectile.getyCoordinate();

            Plant plant = level.getPlantAt(x, y);
            if(plant != null && plant.isFullyFrozen()){
                if(projectile.getCreatorPlantCategory().hasThisTag(PlantTag.FIRE)){
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
                    plant.decreaseIceHP((int) (6)); // later on work with delta time;
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
    public void WaveStarted(Level level, int waveNumber) {
        Random random = new Random();
         int randomRow = random.nextInt(5);
        int affectedRows = random.nextInt(2) + 1;
        for (int i = 0; i < affectedRows; i++) {
            for (Plant plant : level.getActivePlants()) {
                if (plant.getY() == randomRow && !plant.hasThisTag(PlantTag.FIRE)) {
                    plant.addFreezeLevel(1);
                }
            }
        }

    }

    @Override
    public void PlantPlaced(Level level, Plant plant, int x, int y) {
        //check with game manager; may switch planting plants here later on
    }
}



