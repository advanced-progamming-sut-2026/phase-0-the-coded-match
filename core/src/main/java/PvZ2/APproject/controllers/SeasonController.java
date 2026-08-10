package controllers;

import models.Level;
import models.seasons.Season;
import models.seasons.SeasonData;
import models.seasons.SeasonFactory;
import models.seasons.SeasonRepository;

import java.util.ArrayList;
import java.util.List;

public class SeasonController {
    private static SeasonController instance;
    private final List<Season> activeSeasons = new ArrayList<>();
    private Season currentSeason;

    private SeasonController() {
        loadAllSeasons();
    }

    public static SeasonController getInstance() {
        if (instance == null) {
            instance = new SeasonController();
        }
        return instance;
    }

    public void loadAllSeasons() {
        activeSeasons.clear();
        List<SeasonData> allData = SeasonRepository.getInstance().getAllSeasons();
        for (SeasonData data : allData) {
            Season season = SeasonFactory.createSeason(data);
            if (season != null) {
                activeSeasons.add(season);
            }
        }
        if (!activeSeasons.isEmpty()) {
            currentSeason = activeSeasons.get(0);
        }
    }
    public void startLevel(Level level, String seasonType) {
        Season season = getSeasonByType(seasonType);
        if (season == null) {
            season = currentSeason;
        }

        if (season != null && level != null) {
            this.currentSeason = season;
            level.setCurrentSeason(season);
            season.LevelStarted(level);
        }
    }

    public Season getSeasonByType(String seasonType) {
        for (Season season : activeSeasons) {
            if (season.getType() != null && season.getType().name().equalsIgnoreCase(seasonType)) {
                return season;
            }
        }
        return null;
    }
    public Season getCurrentSeason() { return currentSeason; }
    public List<Season> getActiveSeasons() { return activeSeasons; }

}
