package models.seasons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SeasonRepository {
    private final List<SeasonData> seasons;

    public SeasonRepository(String jsonPath) {
        String path = new File(jsonPath).exists() ? jsonPath : "src/" + jsonPath;
        seasons = loadSeasons(path);
    }

    private List<SeasonData> loadSeasons(String jsonPath) {
        try (FileReader reader = new FileReader(jsonPath)) {
            Type listType = new TypeToken<ArrayList<SeasonData>>() { }.getType();
            List<SeasonData> loaded = new Gson().fromJson(reader, listType);
            if (loaded == null) {
                return new ArrayList<>();
            }
            for (SeasonData season : loaded) {
                for (models.LevelData level : season.getLevels()) {
                    if (level.getMap() != null) {
                        level.getMap().initializeGrid();
                    }
                }
            }
            return loaded;
        } catch (IOException | RuntimeException e) {
            System.err.println("Failed to load seasons JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<SeasonData> getAllSeasons() {
        return seasons;
    }

    public SeasonData findById(int id) {
        for (SeasonData season : seasons) {
            if (season.getId() == id) {
                return season;
            }
        }
        return null;
    }

    public SeasonData findByType(String seasonType) {
        for (SeasonData season : seasons) {
            if (season.getSeasonType().equalsIgnoreCase(seasonType)) {
                return season;
            }
        }
        return null;
    }
}
