package PvZ2.APproject.enums;

public enum SunType {
    NORMAL("Normal" ,80, 25),
    SPECIAL("Special" ,15, 100),
    RADIOACTIVE("RadioActive" ,5, 25);
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
}
