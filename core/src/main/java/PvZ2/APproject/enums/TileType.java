package enums;

public enum TileType {
    NORMAL(true, 0),
    GRAVE(false, 700),
    ICE(false, 600),
    SLIDE_UP(false, 0),
    SLIDE_DOWN(false, 0),
    WATER(true, 0),
    LOW_TIDE(true, 0),
    NECROMANCY(true, 0),
    CRATER(false, 0);

    private final boolean canPlant;
    private final int maxHp;

    TileType(boolean canPlant, int maxHp) {
        this.canPlant = canPlant;
        this.maxHp = maxHp;
    }

    public boolean isCanPlant() {
        return canPlant;
    }

    public int getMaxHp() {
        return maxHp;
    }
}
