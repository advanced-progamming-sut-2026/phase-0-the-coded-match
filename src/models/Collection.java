package models;

import models.MiniGameRelated.MiniGame;
import models.plants.Plant;
import models.plants.PlantData;
import models.plants.PlantRepository;

import java.util.ArrayList;
import java.util.List;

public class Collection {
    private List<String> availableZombiesIds;
    private List<String> availablePlantsIds;
    private transient List<Plant> availablePlants;
    private transient List<MiniGame> unlockedMinigames;
    private transient List<Level> unlockedLevels;

    public Collection() {
        initialize();
    }

    private void initialize() {
        if (availableZombiesIds == null) {
            availableZombiesIds = new ArrayList<>();
        }
        if (availablePlantsIds == null) {
            availablePlantsIds = new ArrayList<>();
        }
        if (availablePlants == null) {
            availablePlants = new ArrayList<>();
        }
        if (availablePlants.isEmpty() && !availablePlantsIds.isEmpty()) {
            for (String plantId : availablePlantsIds) {
                PlantData data = PlantRepository.getInstance().findById(plantId);
                if (data != null) {
                    availablePlants.add(new Plant(data, 0, 0, 1));
                }
            }
        }
        if (unlockedMinigames == null) {
            unlockedMinigames = new ArrayList<>();
        }
        if (unlockedLevels == null) {
            unlockedLevels = new ArrayList<>();
        }
    }

    public List<String> getAvailableZombiesIds() {
        initialize();
        return availableZombiesIds;
    }

    public void unlockZombie(String zombieId) {
        initialize();
        if (zombieId != null && !availableZombiesIds.contains(zombieId)) {
            availableZombiesIds.add(zombieId);
        }
    }

    public List<String> getAvailablePlantsIds() {
        initialize();
        return availablePlantsIds;
    }

    public List<Plant> getAvailablePlants() {
        initialize();
        return availablePlants;
    }

    public void addPlant(Plant plant) {
        initialize();
        if (plant != null) {
            availablePlants.add(plant);
            String plantId = plant.getData().getId();
            if (plantId != null && !availablePlantsIds.contains(plantId)) {
                availablePlantsIds.add(plantId);
            }
        }
    }

    public void unlockPlant(String plantId) {
        initialize();
        if (plantId != null && !availablePlantsIds.contains(plantId)) {
            availablePlantsIds.add(plantId);
        }
    }

    public List<MiniGame> getUnlockedMinigames() {
        initialize();
        return unlockedMinigames;
    }

    public void setUnlockedMinigames(List<MiniGame> unlockedMinigames) {
        this.unlockedMinigames = unlockedMinigames == null ? new ArrayList<>() : unlockedMinigames;
    }

    public List<Level> getUnlockedLevels() {
        initialize();
        return unlockedLevels;
    }

    public void setUnlockedLevels(List<Level> unlockedLevels) {
        this.unlockedLevels = unlockedLevels == null ? new ArrayList<>() : unlockedLevels;
    }
}
