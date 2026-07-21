package models;

import models.MiniGameRelated.MiniGame;

import java.util.ArrayList;
import java.util.List;

public class Collection {
     private List<String> availableZombiesIds;
     private List<String> availablePlantsIds;
     private List<MiniGame> unlockedMinigames;
     private List<Level> unlockedLevels;

     public Collection() {
          this.availableZombiesIds = new ArrayList<>();
          this.availablePlantsIds = new ArrayList<>();
          this.unlockedMinigames = new ArrayList<>();
          this.unlockedLevels = new ArrayList<>();
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