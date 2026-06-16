package models.plants;

import java.util.ArrayList;
import java.util.List;

public class PlantRepository {
    private final List<PlantData> plants;

    public PlantRepository(String jsonPath) {
        this.plants = loadPlants(jsonPath);
    }

    private List<PlantData> loadPlants(String jsonPath) {
        return new ArrayList<>();
    }

    public List<PlantData> getAllPlants() {
        return plants;
    }

    public PlantData findByName(String name) {
        for (PlantData plant : plants) {
            if (plant.getName().equalsIgnoreCase(name)
                    || plant.getDisplayName().equalsIgnoreCase(name)) {
                return plant;
            }
        }
        return null;
    }

    public PlantData findById(String id) {
        for (PlantData plant : plants) {
            if (plant.getId().equalsIgnoreCase(id)) {
                return plant;
            }
        }
        return null;
    }

    public List<PlantData> findByCategory(String category) {
        List<PlantData> result = new ArrayList<>();
        for (PlantData plant : plants) {
            if (plant.getCategory().equalsIgnoreCase(category)) {
                result.add(plant);
            }
        }
        return result;
    }

    public List<PlantData> findByTag(String tag) {
        List<PlantData> result = new ArrayList<>();
        for (PlantData plant : plants) {
            if (plant.getTags() != null && plant.getTags().contains(tag)) {
                result.add(plant);
            }
        }
        return result;
    }
}