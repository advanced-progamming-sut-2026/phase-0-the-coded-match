package PvZ2.APproject.models.plants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlantRepository {
    private static PlantRepository instance;
    private final List<PlantData> plants;

    public PlantRepository() {
        this.plants = loadPlants("assets/Plants.json");
    }

    public static PlantRepository getInstance() {
        if (instance == null) {
            instance = new PlantRepository();
        }
        return instance;
    }

    private List<PlantData> loadPlants(String jsonPath) {
        try {
            FileHandle file = Gdx.files.internal(jsonPath);
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<PlantData>>() {}.getType();
            List<PlantData> result = gson.fromJson(file.readString(), listType);

            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<PlantData> getAllPlants() {
        return plants;
    }

    public PlantData findByName(String name) {
        if (name == null) {
            return null;
        }

        for (PlantData plant : plants) {
            if (plant.getName() != null && plant.getName().equalsIgnoreCase(name)) {
                return plant;
            }
            if (plant.getDisplayName() != null && plant.getDisplayName().equalsIgnoreCase(name)) {
                return plant;
            }
        }
        return null;
    }

    public PlantData findById(String id) {
        if (id == null) {
            return null;
        }

        for (PlantData plant : plants) {
            if (plant.getId() != null && plant.getId().equalsIgnoreCase(id)) {
                return plant;
            }
        }
        return null;
    }

    public List<PlantData> findByCategory(String category) {
        List<PlantData> result = new ArrayList<>();
        if (category == null) {
            return result;
        }

        for (PlantData plant : plants) {
            if (plant.getCategory() == null) {
                continue;
            }
            if (plant.getCategory().name().equalsIgnoreCase(category)
                    || plant.getCategory().getName().equalsIgnoreCase(category)) {
                result.add(plant);
            }
        }
        return result;
    }

    public List<PlantData> findByTag(String tag) {
        List<PlantData> result = new ArrayList<>();
        if (tag == null) {
            return result;
        }

        for (PlantData plant : plants) {
            if (plant.getTags() != null && plant.getTags().contains(tag)) {
                result.add(plant);
            }
        }
        return result;
    }
}
