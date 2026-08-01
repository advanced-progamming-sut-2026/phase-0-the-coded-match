package models.seasons;

public class SeasonFactory {
    public static Season createSeason(SeasonData data) {
        if (data == null || data.getSeasonType() == null) {
            return null;
        }
        String typeStr = data.getSeasonType().toUpperCase();

        switch (typeStr) {
            case "ANCIENT_EGYPT":
            case "EGYPT":
                return new AncientEgypt(data);
            case "FROSTBITE_CAVES":
            case "FROSTBITE":
                return new FrostbiteCaves(data);
            case "BIG_WAVE_BEACH":
            case "BEACH":
                return new BigWaveBeach(data);
            case "DARK_AGES":
            case "DARK":
                return new DarkAges(data);
            default:
                System.err.println("Unknown season type: " + typeStr);
                return null;
        }
    }
}
