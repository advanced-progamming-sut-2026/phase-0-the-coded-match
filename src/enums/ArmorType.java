package enums;

public enum ArmorType {
    CONE("cone", 370, false),
    BUCKET("bucket", 1100, true),
    BRICK("brick", 2200, false),
    SHOULDER_ARMOR("shoulderArmor", 1600, true),
    CROWN("crown", 1600, true),
    NEWSPAPER("newspaper", 800, false),
    ARCADE_MACHINE("arcadeMachine", 1100, false);
    private final String name;
    private final int hp;
    private final boolean metallic;

    ArmorType(String name, int hp, boolean metallic) {
        this.name = name;
        this.hp = hp;
        this.metallic = metallic;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public boolean isMetallic() {
        return metallic;
    }
}
