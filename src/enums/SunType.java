package enums;

public enum SunType {
    NORMAL() {
        @Override
        public void explode() {

        }
    },
    SPECIAL() {
        @Override
        public void explode() {

        }
    },
    RADIOACTIVE() {
        @Override
        public void explode() {

        }
    };
    private final int dropChancePercentage;

    SunType(int dropChancePercentage) {
        this.dropChancePercentage = dropChancePercentage;
    }

    public int getDropChancePercentage() {
        return dropChancePercentage;
    }

    public abstract void explode();
}
