package PvZ2.APproject.controllers;

import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.plants.PlantData;

public class PlantSelectionController {
    private final Level level;

    private PlantData selectedPlant;
    private Tile hoveredTile;

    public PlantSelectionController(Level level) {
        this.level = level;
    }

    public void selectPlant(PlantData plantData) {
        selectedPlant = plantData;
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

        Plant tempPlant = new Plant(
            selectedPlant,
            hoveredTile.getColumn(),
            hoveredTile.getRow(),
            1
        );

        return PlantController.canPlaceOnTile(
            tempPlant,
            hoveredTile
        );
    }

    public String tryPlaceSelectedPlant() {
        if (selectedPlant == null) {
            return "No plant selected";
        }

        if (hoveredTile == null) {
            return "No tile selected";
        }

        String error = plantController.plantPlant(
            selectedPlant.getName(),
            hoveredTile.getColumn(),
            hoveredTile.getRow()
        );

        if (error == null) {
            cancelSelection();
        }

        return error;
    }

}
