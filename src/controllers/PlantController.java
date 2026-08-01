package controllers;

import enums.PlantTag;
import enums.SunType;
import enums.TileType;
import models.Level;
import models.Sun;
import models.GameMapRelated.Tile;
import models.plants.Plant;
import models.plants.PlantData;
import models.plants.PlantRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlantController {
    private final PlantRepository plantRepository;
    private final List<Plant> activePlants;

    public PlantController() {
        this.plantRepository = PlantRepository.getInstance();
        this.activePlants = new ArrayList<>();
    }

    public Plant plantPlant(String plantName, int x, int y, int level) {
        PlantData data = plantRepository.findByName(plantName);
        if (data == null) {
            return null;
        }
        return new Plant(data, x, y, level);
    }

    public void removeDeadPlants() {
        Iterator<Plant> iterator = activePlants.iterator();
        while (iterator.hasNext()) {
            Plant plant = iterator.next();
            if (plant.isDead()) {
                iterator.remove();
            }
        }
    }

    public void updatePlants() {
        for (Plant plant : activePlants) {
            plant.update();
        }
        removeDeadPlants();
    }

    public void showPlantsInfo() {
        for (Plant plant : activePlants) {
            System.out.println(plant.getData().getDisplayName() + " at (" + plant.getX() + ", " + plant.getY()
                    + ") hp=" + plant.getCurrentHp());
        }
    }

    public static void produceSun(Plant plant) {
        plant.setProducedSun(true);
        Sun sun = new Sun(plant.getX(), plant.getY(), SunType.NORMAL.getValue(), 0, false, SunType.NORMAL, plant);
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level != null) {
            level.getActiveSuns().add(sun);
        }
    }

    public static boolean canPlaceOnTile(Plant plant, Tile tile) {
        return plant != null && tile != null && tile.isPlantable(plant);
    }

    public static void shootProjectile() {
    }

    public static void lobProjectile() {
    }

    public static void explode() {
    }

    public static void attack() {
    }

    public static void detectAZombie() {
    }

    public static void givePlantFood() {
    }
}