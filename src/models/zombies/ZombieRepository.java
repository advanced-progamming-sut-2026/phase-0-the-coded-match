package models.zombies;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enums.SeasonType;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ZombieRepository {
    private static ZombieRepository instance;
    private List<ZombieData> zombies;

    public ZombieRepository() {
        loadZombies("assets/Zombies.json");
    }

    public static ZombieRepository getInstance() {
        if (instance == null) {
            instance = new ZombieRepository();
        }
        return instance;
    }

    private void loadZombies(String jsonPath) {
        try (FileReader reader = new FileReader(jsonPath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<ZombieData>>(){}.getType();
            this.zombies = gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
            this.zombies = new ArrayList<>();
        }
    }

    public List<ZombieData> getAllZombies() {
        return zombies;
    }

    public ZombieData findByDisplayName(String name) {
        for (ZombieData zombie : zombies) {
            if (zombie.getDisplayName() != null && zombie.getDisplayName().equalsIgnoreCase(name)) {
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

    public List<ZombieData> findBySeason(SeasonType season) {
        List<ZombieData> result = new ArrayList<>();
        for (ZombieData zombie : zombies) {
            if (zombie.getSeasons() != null && zombie.getSeasons().contains(season)) {
                result.add(zombie);
            }
        }
        return result;
    }
}