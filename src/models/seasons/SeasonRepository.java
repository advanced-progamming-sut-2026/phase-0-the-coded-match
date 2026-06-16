package models.seasons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class SeasonRepository {
    private final List<SeasonData> seasons;

    public SeasonRepository(String jsonPath) {
        this.seasons = loadSeasons(jsonPath);
    }

    private List<SeasonData> loadSeasons(String jsonPath) {
       //TODO
        return null;
    }

    public List<SeasonData> getAllSeasons() {
        return seasons;
    }

    public SeasonData findById(String id) {
        //TODO
        return null;
    }

    public SeasonData findByType(String seasonType) {
       //TODO
        return null;
    }
}