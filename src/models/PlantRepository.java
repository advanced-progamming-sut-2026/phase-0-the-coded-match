package models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class PlantRepository {
    private final List<PlantData> plants;

    public PlantRepository(String jsonPath) {
        this.plants = loadPlants(jsonPath);
    }

    private List<PlantData> loadPlants(String jsonPath) {
        //TODO
        return null;
    }

    public List<PlantData> getAllPlants() {
        return plants;
    }

    public PlantData findByName(String name) {
        //TODO
        return null;
    }

    public PlantData findById(String id) {
        //TODO
        return null;
    }

    public List<PlantData> findByCategory(String category) {
        //TODO
        return null;
    }

    public List<PlantData> findByTag(String tag) {
        //TODO
        return null;
    }
}