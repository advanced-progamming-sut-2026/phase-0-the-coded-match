package Model;

import java.util.List;

public class Collection {
     private List<Zombie> zombies;
     private List<Plant> plants;
     private List<MiniGame> unlockedMinigames;
     private List<Chapter> unlockedChapters;

     public List<Zombie> getZombies() {
          return zombies;
     }

     public void setZombies(List<Zombie> zombies) {
          this.zombies = zombies;
     }

     public List<Plant> getPlants() {
          return plants;
     }

     public void setPlants(List<Plant> plants) {
          this.plants = plants;
     }

     public List<MiniGame> getUnlockedMinigames() {
          return unlockedMinigames;
     }

     public void setUnlockedMinigames(List<MiniGame> unlockedMinigames) {
          this.unlockedMinigames = unlockedMinigames;
     }

     public List<Chapter> getUnlockedChapters() {
          return unlockedChapters;
     }

     public void setUnlockedChapters(List<Chapter> unlockedChapters) {
          this.unlockedChapters = unlockedChapters;
     }
}
