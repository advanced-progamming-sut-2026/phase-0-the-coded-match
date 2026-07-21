package models.specialLevels;

import controllers.GameManagerController;
import models.GameMapRelated.Tile;
import models.Level;
import models.plants.Plant;
import models.plants.PlantRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SaveOurSeedsStrategy implements SpecialLevelStrategy{
    private final List<Plant> protectedPlantsList = new ArrayList<>();
    @Override
    public void levelStart(Level level) {
        Random random = new Random();
        int rows = level.getGameMap().getRows();
        int cols = level.getGameMap().getColumns();

        int protectedPlants = level.getData().getProtectedPlants().size();
        for(int i =0; i < protectedPlants; i++){
            int randomRow = random.nextInt(rows);
            int randomCol = random.nextInt(cols);
            Tile tile = level.getGameMap().getTile(randomRow, randomCol);
            if (tile != null && tile.getPlant() == null ){
                for (String data : level.getData().getProtectedPlants()) {
                    Plant protectedPlant = new Plant(PlantRepository.getInstance().findByName(data), randomCol, randomRow, level.getLevelNumber());
                    level.getActivePlants().add(protectedPlant);
                    protectedPlantsList.add(protectedPlant);
                }
            }
        }


    }

    @Override
    public void update(Level level) {

    }

    @Override
    public void plantLost(Level level, Plant plant) {
        if (protectedPlantsList.contains(plant)) {
            GameManagerController.getInstance().gameOver();
        }
    }
}
