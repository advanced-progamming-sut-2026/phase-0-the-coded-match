package controllers;

import models.plants.Plant;
import models.plants.PlantRepository;

import java.nio.file.attribute.UserPrincipalLookupService;
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

    public static void produceSun(Plant plant) {
        plant.setProducedSun(true);
    }

    public static void shootProjectile() {

    }

    public static void lobProjectile() {

    }

    public static void explode() {

    }

    public static void attack() {

    }

    public static void detectAZombie() {}

    public static void givePlantFood() {}

}