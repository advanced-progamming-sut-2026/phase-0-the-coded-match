package PvZ2.APproject.models;

import PvZ2.APproject.models.MiniGameRelated.MiniGame;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;

import java.util.ArrayList;
import java.util.List;

public class Collection {
     private List<String> availableZombiesIds;
     private List<String> availablePlantsIds;
     private List<Plant> availablePlants;
     private List<MiniGame> unlockedMinigames;
     private List<Level> unlockedLevels;

     public Collection() {
          this.availableZombiesIds = new ArrayList<>();
          this.availablePlantsIds = new ArrayList<>();
          for (String name : List.of("Peashooter", "Sunflower", "Wall-nut")) {
             PlantData p = PlantRepository.getInstance().findByName(name);
             if (p != null) {
                 unlockPlant(p.getId());
             }
          }
          this.availablePlants = new ArrayList<>();
          this.unlockedMinigames = new ArrayList<>();
          this.unlockedLevels = new ArrayList<>();
     }

     public void ensure() {
         if (availableZombiesIds == null) availableZombiesIds = new ArrayList<>();
         if (availablePlantsIds == null) availablePlantsIds = new ArrayList<>();
         if (availablePlants == null) availablePlants = new ArrayList<>();
         if (unlockedMinigames == null) unlockedMinigames = new ArrayList<>();
         if (unlockedLevels == null) unlockedLevels = new ArrayList<>();
     }

     public List<String> getAvailableZombiesIds() {
          return availableZombiesIds;
     }

     public void unlockZombie(String zombieId) {
          if (!availableZombiesIds.contains(zombieId)) {
               availableZombiesIds.add(zombieId);
          }
     }

     public List<String> getAvailablePlantsIds() {
          return availablePlantsIds;
     }

     public List<Plant> getAvailablePlants() {
          return availablePlants;
     }

     public void addPlant(Plant plant) {
          if (plant != null) {
               availablePlants.add(plant);
               if (!availablePlantsIds.contains(plant.getData().getId())) {
                    availablePlantsIds.add(plant.getData().getId());
               }
          }
     }

     public void unlockPlant(String plantId) {
          if (!availablePlantsIds.contains(plantId)) {
               availablePlantsIds.add(plantId);
          }
     }

     public List<MiniGame> getUnlockedMinigames() {
          return unlockedMinigames;
     }

     public void setUnlockedMinigames(List<MiniGame> unlockedMinigames) {
          this.unlockedMinigames = unlockedMinigames;
     }

     public List<Level> getUnlockedLevels() {
          return unlockedLevels;
     }

     public void setUnlockedLevels(List<Level> unlockedLevels) {
          this.unlockedLevels = unlockedLevels;
     }
}
