package enums;

public enum ArmorType {
    CONE("cone"),
    BUCKET("bucket"),
    HELMET("helmet"),
    SHOULDER_ARMOR("shoulderArmor"),
    BLOCK("block"),
    NEWSPAPER("newspaper"),
    BARREL("barrel"),
    ARCADE_MACHINE("arcadeMachine");
    private final String name;

    ArmorType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
