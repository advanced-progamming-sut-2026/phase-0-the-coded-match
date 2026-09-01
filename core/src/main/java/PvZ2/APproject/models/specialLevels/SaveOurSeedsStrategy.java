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
        List<String> protectedPlants = level.getData().getProtectedPlants();
        if (protectedPlants == null || protectedPlants.isEmpty()) return;
        List<Tile> availableTiles = new ArrayList<>();
        for (int row = 1; row <= level.getGameMap().getRows(); row++) {
            for (int col = 1; col <= level.getGameMap().getColumns(); col++) {
                Tile tile = level.getGameMap().getTile(col, row);
                if (tile != null && tile.getPlant() == null && !tile.isGrave()) availableTiles.add(tile);
            }
        }
        java.util.Collections.shuffle(availableTiles);
        int index = 0;
        for (String name : protectedPlants) {
            if (index >= availableTiles.size()) break;
            PvZ2.APproject.models.plants.PlantData data = PlantRepository.getInstance().findByName(name);
            if (data == null) continue;
            Tile tile = availableTiles.get(index++);
            Plant protectedPlant = new Plant(data, tile.getColumn(), tile.getRow(), level.getLevelNumber());
            tile.setPlant(protectedPlant);
            level.getActivePlants().add(protectedPlant);
            protectedPlantsList.add(protectedPlant);
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

    @Override
    public List<Plant> getProtectedPlantsList() {
        return protectedPlantsList;
    }
}
