package enums;

public enum SunType {
    NORMAL("Normal" ,80, 25) {
        @Override
        public void explode() {}
    },
    SPECIAL("Special" ,15, 100) {
        @Override
        public void explode() {}
    },
    RADIOACTIVE("RadioActive" ,5, 25) {
        @Override
        public void explode() {

        }
    };
    private final String name;
    private final int dropChancePercentage;
    private final int value;

    SunType(String name, int dropChancePercentage, int value) {
        this.name = name;
        this.dropChancePercentage = dropChancePercentage;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getDropChancePercentage() {
        return dropChancePercentage;
    }

    public int getValue() {
        return value;
    }

    public abstract void explode();
}
