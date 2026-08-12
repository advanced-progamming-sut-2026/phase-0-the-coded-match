package PvZ2.APproject.enums;

public enum PlantCategory {
    SUN_PRODUCER("Sun Producer"),
    SHOOTER("Shooter"),
    HOMING("Homing"),
    STRIKE_TROUGH("Strike-through"),
    LOBBER("Lobber"),
    EXPLOSIVE("Explosive"),
    MELEE("melee"),
    WALL_NUT("Wall-nut"),
    MODIFIER("Modifier"),
    MINT("Mint");
    private final String name;

    PlantCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
