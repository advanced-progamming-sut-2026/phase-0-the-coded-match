package models;

import models.MiniGameRelated.MiniGame;
import models.plants.Plant;
import models.zombies.Zombie;

import java.util.List;

public class Collection {
     private List<Zombie> availableZombies;
     private List<Plant> availablePlants;
     private List<MiniGame> unlockedMinigames;
     private List<Level> unlockedLevels;

     public List<Zombie> getAvailableZombies() {
          return availableZombies;
     }

     public void setAvailableZombies(List<Zombie> availableZombies) {
          this.availableZombies = availableZombies;
     }

     public List<Plant> getAvailablePlants() {
          return availablePlants;
     }

     public void setAvailablePlants(List<Plant> availablePlants) {
          this.availablePlants = availablePlants;
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
