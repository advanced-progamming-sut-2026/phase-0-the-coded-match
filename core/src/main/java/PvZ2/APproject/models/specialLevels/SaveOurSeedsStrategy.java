package PvZ2.APproject.models.specialLevels;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.plants.PlantRepository;

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
