package PvZ2.APproject.models.zombies;

import PvZ2.APproject.enums.SeasonType;
import PvZ2.APproject.enums.ZombieState;

import java.util.List;

public class ZombossData extends ZombieData {
    private final SeasonType season;
    private final String path;

    public ZombossData(SeasonType season) {
        this.season = season == null ? SeasonType.ANCIENT_EGYPT : season;
        this.path = switch (this.season) {
            case ANCIENT_EGYPT -> "ZOMBIE_EGYPT_ZOMBOSS";
            case FROSTBITE_CAVES -> "ZOMBIE_ICEAGE_ZOMBOSS";
            case BIG_WAVE_BEACH -> "ZOMBIE_BEACH_ZOMBOSS";
            case DARK_AGES -> "ZOMBIE_DARK_ZOMBOSS";
        };
    }

    @Override
    public String getId() {
        return path;
    }

    @Override
    public String getDisplayName() {
        return "Zomboss";
    }

    @Override
    public List<SeasonType> getSeasons() {
        return List.of(season);
    }

    @Override
    public int getHp() {
        return 9000;
    }

    @Override
    public int getEatDPS() {
        return 0;
    }

    @Override
    public double getSpeed() {
        return 0;
    }

    @Override
    public double getAttackInterval() {
        return 1;
    }

    @Override
    public int getWaveCost() {
        return 0;
    }

    @Override
    public List<ZombieArmorData> getArmors() {
        return null;
    }

    @Override
    public String getBehaviorType() {
        return null;
    }

    @Override
    public ZombieState getState() {
        return ZombieState.IDLE;
    }

    @Override
    public boolean isHasParasol() {
        return false;
    }

    @Override
    public double getRunningSpeed() {
        return 0;
    }

    @Override
    public int getCost() {
        return 0;
    }

    public SeasonType getSeason() {
        return season;
    }

    @Override
    public String getPath() {
        return path;
    }
}
