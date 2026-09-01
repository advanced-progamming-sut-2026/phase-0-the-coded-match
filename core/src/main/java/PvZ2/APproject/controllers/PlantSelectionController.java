package PvZ2.APproject.controllers;

import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.PlantData;

public class PlantSelectionController {
    private final Level level;
    private PlantController plantController;

    private PlantData selectedPlant;
    private Tile hoveredTile;

    public PlantSelectionController(Level level) {
        this.level = level;
        this.plantController = new PlantController();
    }

    public String selectPlant(PlantData plantData) {
        if (plantData.getSunCost() > level.getCollectedSunsAmount()) {
            return "You don't have enough suns";
        }
        selectedPlant = plantData;
        return "";
    }

    public void cancelSelection() {
        selectedPlant = null;
        hoveredTile = null;
    }

    public PlantData getSelectedPlant() {
        return selectedPlant;
    }

    public boolean hasSelectedPlant() {
        return selectedPlant != null;
    }

    public void setHoveredTile(Tile tile) {
        hoveredTile = tile;
    }

    public Tile getHoveredTile() {
        return hoveredTile;
    }

    public boolean isHoveredTileValid() {
        if (selectedPlant == null || hoveredTile == null) {
            return false;
        }
        return PlantController.getPlantingError(
            selectedPlant.getName(),
            hoveredTile.getColumn(),
            hoveredTile.getRow()
        ) == null;
    }

    public String tryPlaceSelectedPlant() {
        if (selectedPlant == null) {
            return "No plant selected";
        }

        if (hoveredTile == null) {
            return "No tile selected";
        }

        String error = PlantController.plantPlant(
            selectedPlant.getName(),
            hoveredTile.getColumn(),
            hoveredTile.getRow()
        );

        if (error == null) {
            cancelSelection();
        }

        return error;
    }

    public PlantController getPlantController() {
        return plantController;
    }
}
