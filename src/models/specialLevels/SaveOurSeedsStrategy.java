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

        for(String data : level.getData().getProtectedPlants()) {
            boolean placed = false;
            while(!placed) {
                int randomRow = 1 + random.nextInt(rows);
                int randomCol = 1 + random.nextInt(cols);
                Tile tile = level.getGameMap().getTile(randomCol, randomRow);
                if (tile != null && tile.getPlant() == null) {
                    Plant protectedPlant = new Plant(PlantRepository.getInstance().findByName(data), randomCol, randomRow, level.getLevelNumber());
                    tile.setPlant(protectedPlant);
                    level.getActivePlants().add(protectedPlant);
                    protectedPlantsList.add(protectedPlant);
                    placed = true;
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
