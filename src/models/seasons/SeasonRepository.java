package models.seasons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SeasonRepository {
    private final List<SeasonData> seasons;

    public SeasonRepository(String jsonPath) {
        this.seasons = loadSeasons(jsonPath);
    }

    private List<SeasonData> loadSeasons(String jsonPath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(jsonPath)) {
            Type listType = new TypeToken<ArrayList<SeasonData>>(){}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            System.err.println("Failed to load seasons JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<SeasonData> getAllSeasons() {
        return seasons;
    }

    public SeasonData findById(int id) {
        for( SeasonData season : seasons ){
            if(season.getId() == id){
                return season;
            }
        }
        return null;
    }

    public SeasonData findByType(String seasonType) {
       for(SeasonData season : seasons){
           if(season.getSeasonType().equalsIgnoreCase(seasonType)){
               return season;
           }
       }
        return null;
    }
}