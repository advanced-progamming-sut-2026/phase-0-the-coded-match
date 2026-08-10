package enums;

public enum SeasonType {
    ANCIENT_EGYPT("ancient egypt"),
    FROSTBITE_CAVES("frostbite caves"),
    BIG_WAVE_BEACH("big wave beach"),
    DARK_AGES("dark ages");
    private final String name;

    SeasonType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
