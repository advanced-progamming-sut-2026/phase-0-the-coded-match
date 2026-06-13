package enums;

public enum TileType {
    NORMAL(true),
    GRAVE(false) {
        @Override
        public void takeDamage(int damage) {

        }
    },
    ICE(false) {
        @Override
        public void takeDamage(int damage) {

        }
    },
    SLIDE_UP(false),
    SLIDE_DOWN(false),
    WATER,
    LOW_TIDE,
    NECROMANCY;

    private final boolean canPlant;
    private final int hp;

    TileType(boolean canPlant, int hp) {
        this.canPlant = canPlant;
        this.hp = hp;
    }

    public abstract void takeDamage(int damage);
}
