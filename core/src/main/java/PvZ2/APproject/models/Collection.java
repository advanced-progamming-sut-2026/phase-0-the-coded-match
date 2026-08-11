package PvZ2.APproject.models;

import PvZ2.APproject.models.MiniGameRelated.MiniGame;
import PvZ2.APproject.models.plants.Plant;

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
          this.availablePlants = new ArrayList<>();
          this.unlockedMinigames = new ArrayList<>();
          this.unlockedLevels = new ArrayList<>();
     }

     private void ensure() { if (availableZombiesIds == null) availableZombiesIds = new ArrayList<>(); if (availablePlantsIds == null) availablePlantsIds = new ArrayList<>(); if (availablePlants == null) availablePlants = new ArrayList<>(); if (unlockedMinigames == null) unlockedMinigames = new ArrayList<>(); if (unlockedLevels == null) unlockedLevels = new ArrayList<>(); }

     public List<String> getAvailableZombiesIds() {
          ensure();
          return availableZombiesIds;
     }

     public void unlockZombie(String zombieId) {
          ensure();
          if (!availableZombiesIds.contains(zombieId)) {
               availableZombiesIds.add(zombieId);
          }
     }

     public List<String> getAvailablePlantsIds() {
          ensure();
          return availablePlantsIds;
     }

     public List<Plant> getAvailablePlants() {
          ensure();
          return availablePlants;
     }

     public void addPlant(Plant plant) {
          ensure();
          if (plant != null) {
               availablePlants.add(plant);
               if (!availablePlantsIds.contains(plant.getData().getId())) {
                    availablePlantsIds.add(plant.getData().getId());
               }
          }
     }

     public void unlockPlant(String plantId) {
          ensure();
          if (!availablePlantsIds.contains(plantId)) {
               availablePlantsIds.add(plantId);
          }
     }

     public List<MiniGame> getUnlockedMinigames() {
          ensure();
          return unlockedMinigames;
     }

     public void setUnlockedMinigames(List<MiniGame> unlockedMinigames) {
          this.unlockedMinigames = unlockedMinigames;
     }

     public List<Level> getUnlockedLevels() {
          ensure();
          return unlockedLevels;
     }

     public void setUnlockedLevels(List<Level> unlockedLevels) {
          this.unlockedLevels = unlockedLevels;
     }
}
