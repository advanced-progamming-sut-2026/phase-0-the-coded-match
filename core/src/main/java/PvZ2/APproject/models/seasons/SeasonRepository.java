package PvZ2.APproject.models.seasons;


import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import PvZ2.APproject.utils.AssetPaths;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SeasonRepository {
    private static SeasonRepository instance;
    private final List<SeasonData> seasons;

    public SeasonRepository(String jsonPath) {
        this.seasons = loadSeasons(jsonPath);
    }

    public static SeasonRepository getInstance() {
        if (instance == null) {
            instance = new SeasonRepository("assets/Seasons.json");
        }
        return instance;
    }

    private List<SeasonData> loadSeasons(String jsonPath) {
        Gson gson = new Gson();
        try (Reader reader = AssetPaths.reader(jsonPath)) {
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
        if (seasonType == null) return null;
       for(SeasonData season : seasons){
           if(season.getSeasonType().toString().equalsIgnoreCase(seasonType)){
               return season;
           }
       }
        return null;
    }
}
