package models.zombies;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enums.SeasonType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ZombieRepository {
    private static ZombieRepository instance;
    private List<ZombieData> zombies;

    public ZombieRepository() {
        loadZombies(resolvePath("assets/Zombies.json"));
    }

    public static ZombieRepository getInstance() {
        if (instance == null) {
            instance = new ZombieRepository();
        }
        return instance;
    }

    private String resolvePath(String path) {
        if (new File(path).exists()) {
            return path;
        }
        return "src/" + path;
    }

    private void loadZombies(String jsonPath) {
        try (FileReader reader = new FileReader(jsonPath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<ZombieData>>() { }.getType();
            List<ZombieData> loaded = gson.fromJson(reader, listType);
            this.zombies = loaded == null ? new ArrayList<>() : loaded;
        } catch (IOException | RuntimeException e) {
            System.err.println("Failed to load zombies JSON: " + e.getMessage());
            this.zombies = new ArrayList<>();
        }
    }

    public List<ZombieData> getAllZombies() {
        return zombies;
    }

    public ZombieData findByDisplayName(String name) {
        if (name == null) {
            return null;
        }
        for (ZombieData zombie : zombies) {
            if (zombie.getDisplayName() != null && zombie.getDisplayName().equalsIgnoreCase(name)) {
                return zombie;
            }
        }
        return findById(name);
    }

    public ZombieData findById(String id) {
        if (id == null) {
            return null;
        }
        for (ZombieData zombie : zombies) {
            if (zombie.getId() != null && zombie.getId().equalsIgnoreCase(id)) {
                return zombie;
            }
        }
        return null;
    }

    public List<ZombieData> findBySeason(SeasonType season) {
        List<ZombieData> result = new ArrayList<>();
        for (ZombieData zombie : zombies) {
            if (zombie.getSeasons().contains(season)) {
                result.add(zombie);
            }
        }
        return result;
    }
}
