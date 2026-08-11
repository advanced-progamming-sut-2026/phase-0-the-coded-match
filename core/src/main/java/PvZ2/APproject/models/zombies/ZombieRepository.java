package PvZ2.APproject.models.zombies;

import PvZ2.APproject.enums.SeasonType;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import PvZ2.APproject.utils.AssetPaths;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class ZombieRepository {
    private static ZombieRepository instance;
    private List<ZombieData> zombies;

    public ZombieRepository() {
        loadZombies("src/assets/Zombies.json");
    }

    public static ZombieRepository getInstance() {
        if (instance == null) {
            instance = new ZombieRepository();
        }
        return instance;
    }

    private void loadZombies(String jsonPath) {
        try (Reader reader = AssetPaths.reader(jsonPath)) {
            Gson gson = new Gson();
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement element : array) if (element.getAsJsonObject().has("armors") && element.getAsJsonObject().get("armors").isJsonObject()) {
                JsonArray armors = new JsonArray(); armors.add(element.getAsJsonObject().get("armors")); element.getAsJsonObject().add("armors", armors);
            }
            Type listType = new TypeToken<ArrayList<ZombieData>>(){}.getType();
            this.zombies = gson.fromJson(array, listType);
        } catch (IOException e) {
            e.printStackTrace();
            this.zombies = new ArrayList<>();
        }
    }

    public List<ZombieData> getAllZombies() {
        return zombies;
    }

    public ZombieData findByDisplayName(String name) {
        String normalizedName = normalize(name);
        if (normalizedName.equals("zombienormal")) normalizedName = "zombiedefault";
        if (normalizedName.equals("zombieconehead")) normalizedName = "zombiearmor1";
        if (normalizedName.equals("zombiebuckethead")) normalizedName = "zombiearmor2";
        for (ZombieData zombie : zombies) {
            if ((zombie.getDisplayName() != null && zombie.getDisplayName().equalsIgnoreCase(name)) || normalize(zombie.getId()).equals(normalizedName)) {
                return zombie;
            }
        }
        return null;
    }

    private String normalize(String value) { return value == null ? "" : value.replaceAll("[^A-Za-z0-9]", "").toLowerCase(); }

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
