package enums.ShopRelated;

public enum PaymentType{
    COIN("coins"),
    GEM("gems");

    private final String name;

    PaymentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
