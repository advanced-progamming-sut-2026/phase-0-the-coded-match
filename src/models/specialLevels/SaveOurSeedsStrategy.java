package models.specialLevels;

import controllers.GameManagerController;
import models.GameMapRelated.Tile;
import models.Level;
import models.plants.Plant;
import models.plants.PlantData;
import models.plants.PlantRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SaveOurSeedsStrategy implements SpecialLevelStrategy {
    private final List<Plant> protectedPlants = new ArrayList<>();

    @Override
    public void levelStart(Level level) {
        List<Tile> emptyTiles = new ArrayList<>();
        for (int y = 1; y <= level.getGameMap().getRows(); y++) {
            for (int x = 1; x <= level.getGameMap().getColumns(); x++) {
                Tile tile = level.getGameMap().getTile(x, y);
                if (tile != null && tile.isEmpty() && tile.getType().isCanPlant()) {
                    emptyTiles.add(tile);
                }
            }
        }
        Collections.shuffle(emptyTiles);
        int index = 0;
        for (String plantName : level.getData().getProtectedPlants()) {
            if (index >= emptyTiles.size()) {
                break;
            }
            PlantData data = PlantRepository.getInstance().findByName(plantName);
            if (data == null) {
                continue;
            }
            Tile tile = emptyTiles.get(index++);
            Plant plant = new Plant(data, tile.getColumn(), tile.getRow(), level.getLevelNumber());
            tile.setPlant(plant);
            level.getActivePlants().add(plant);
            protectedPlants.add(plant);
        }
    }

    @Override
    public void update(Level level) {
    }

    @Override
    public void plantLost(Level level, Plant plant) {
        if (protectedPlants.contains(plant)) {
            GameManagerController.getInstance().gameOver();
        }
    }
}
