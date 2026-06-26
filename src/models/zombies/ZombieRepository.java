package models.zombies;

import java.util.ArrayList;
import java.util.List;

public class ZombieRepository {
    private final List<ZombieData> zombies;
    public static List<ZombieData> getZombies(){
        return zombies;
    }; // temporary until JSONS are made cause we need random zombies for waves

    public ZombieRepository(String jsonPath) {
        this.zombies = loadZombies(jsonPath);
    }

    private List<ZombieData> loadZombies(String jsonPath) {
        return new ArrayList<>();
    }

    public List<ZombieData> getAllZombies() {
        return zombies;
    }

    public ZombieData findByAlias(String alias) {
        for (ZombieData zombie : zombies) {
            if (zombie.getAlias() != null && zombie.getAlias().equalsIgnoreCase(alias)) {
                return zombie;
            }
            if (zombie.getId() != null && zombie.getId().equalsIgnoreCase(alias)) {
                return zombie;
            }
        }
        return null;
    }

    public ZombieData findById(String id) {
        for (ZombieData zombie : zombies) {
            if (zombie.getId().equalsIgnoreCase(id)) {
                return zombie;
            }
        }
        return null;
    }

    public List<ZombieData> findBySeason(String season) {
        List<ZombieData> result = new ArrayList<>();
        for (ZombieData zombie : zombies) {
            if (zombie.getSeasons() != null && zombie.getSeasons().contains(season)) {
                result.add(zombie);
            }
        }
        return result;
    }
}