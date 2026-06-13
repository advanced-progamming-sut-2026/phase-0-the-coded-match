package controllers;

import models.Plant;
import models.PlantRepository;

import java.util.ArrayList;
import java.util.List;

public class PlantController {
    private final PlantRepository plantRepository;
    private final List<Plant> activePlants;

    public PlantController() {
        this.plantRepository = new PlantRepository("assets/Data/plants.json");
        this.activePlants = new ArrayList<>();
    }

    public Plant plantPlant(String plantName, int x, int y, int level) {
        //TODO
        return null;
    }

    public void removeDeadPlants() {
        //TODO
    }

    public void updatePlants() {
       //TODO
    }

    public void showPlantsInfo() {
        //TODO
    }

    public List<Plant> getActivePlants() {
        return activePlants;
    }
}