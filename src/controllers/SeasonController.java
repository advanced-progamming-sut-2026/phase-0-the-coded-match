package controllers;

import enums.SeasonType;
import models.App;
import models.Level;
import models.LevelData;
import models.User;
import models.seasons.AncientEgypt;
import models.seasons.BigWaveBeach;
import models.seasons.DarkAges;
import models.seasons.FrostbiteCaves;
import models.seasons.Season;
import models.seasons.SeasonData;
import models.seasons.SeasonRepository;

import java.util.ArrayList;
import java.util.List;

public class SeasonController {
    public static void loadSeason() {
        SeasonRepository repository = new SeasonRepository("assets/Seasons.json");
        List<Season> seasons = new ArrayList<>();
        for (SeasonData data : repository.getAllSeasons()) {
            SeasonType type = SeasonType.valueOf(data.getSeasonType());
            Season season = switch (type) {
                case ANCIENT_EGYPT -> new AncientEgypt(data);
                case FROSTBITE_CAVES -> new FrostbiteCaves(data);
                case BIG_WAVE_BEACH -> new BigWaveBeach(data);
                case DARK_AGES -> new DarkAges(data);
            };
            boolean firstSeason = seasons.isEmpty();
            season.setUnlocked(firstSeason);
            for (int i = 0; i < data.getLevels().size(); i++) {
                data.getLevels().get(i).setUnlocked(firstSeason && i == 0);
            }
            seasons.add(season);
        }
        App.setAllSeasons(seasons);
        restoreUnlockProgress(seasons);
    }

    private static void restoreUnlockProgress(List<Season> seasons) {
        for (User user : App.getUsers()) {
            int completedSeasonId = user.getLastSeasonId();
            LevelData completedLevel = user.getLastLevel();
            if (completedSeasonId <= 0 || completedLevel == null) {
                continue;
            }
            for (int seasonIndex = 0; seasonIndex < seasons.size(); seasonIndex++) {
                Season season = seasons.get(seasonIndex);
                if (season.getData().getId() > completedSeasonId) {
                    break;
                }
                season.setUnlocked(true);
                for (LevelData level : season.getLevels()) {
                    if (season.getData().getId() < completedSeasonId
                            || level.getLevelNumber() <= completedLevel.getLevelNumber() + 1) {
                        level.setUnlocked(true);
                    }
                }
                if (season.getData().getId() == completedSeasonId
                        && completedLevel.getLevelNumber() >= season.getLevels().size()
                        && seasonIndex + 1 < seasons.size()) {
                    seasons.get(seasonIndex + 1).setUnlocked(true);
                    if (!seasons.get(seasonIndex + 1).getLevels().isEmpty()) {
                        seasons.get(seasonIndex + 1).getLevels().get(0).setUnlocked(true);
                    }
                }
            }
        }
    }

    public static void startLevel(Level level) {
        if (level == null) {
            return;
        }
        level.startLevelMechanics();
        if (level.getCurrentSeason() != null) {
            level.getCurrentSeason().LevelStarted(level);
        }
    }

    public static void advanceWave(Level level, int waveNumber) {
        if (level != null && level.getCurrentSeason() != null) {
            level.getCurrentSeason().WaveStarted(level, waveNumber);
        }
    }

    public static void endLevel(Level level, boolean won) {
        if (!won || level == null || level.getCurrentSeason() == null || App.getCurrentUser() == null) {
            return;
        }
        App.getCurrentUser().setLastSeason(level.getCurrentSeason());
        App.getCurrentUser().setLastLevel(level.getData());
        App.getCurrentUser().addLevelCompleted();
        List<LevelData> levels = level.getCurrentSeason().getLevels();
        int currentIndex = levels.indexOf(level.getData());
        if (currentIndex >= 0 && currentIndex + 1 < levels.size()) {
            levels.get(currentIndex + 1).setUnlocked(true);
            return;
        }
        int seasonIndex = App.getAllSeasons().indexOf(level.getCurrentSeason());
        if (seasonIndex >= 0 && seasonIndex + 1 < App.getAllSeasons().size()) {
            Season nextSeason = App.getAllSeasons().get(seasonIndex + 1);
            nextSeason.setUnlocked(true);
            if (!nextSeason.getLevels().isEmpty()) {
                nextSeason.getLevels().get(0).setUnlocked(true);
            }
        }
    }
}
