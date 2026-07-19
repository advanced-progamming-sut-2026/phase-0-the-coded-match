package enums.ShopRelated;

import static enums.ShopRelated.PaymentType.*;

public enum ShopItemData {
    POT(1, "Pot", 2000, COIN, 1),
    PLANT_FOOD(2, "Plant food", 3, GEM, 1),
    SEED_PACKET_BY_CHANCE(3, "Seed Packet by chance", 1000, COIN, 5),
    SEED_PACKET_BY_CHOICE(4, "Seed Packet by choice", 5, GEM, 10),
    EXCHANGE_CURRENCY(5, "Exchange currency", 5, GEM, 500),
    SEED_PACKET_DAILY(6, "Seed Packet daily", 1600, COIN, 10);

    private final int id;
    private final String name;
    private final int price;
    private final PaymentType paymentType;
    private final int unitBought;

    ShopItemData(int id ,String name, int price, PaymentType paymentType, int unitBought){
        this.id = id;
        this.name=name;
        this.price=price;
        this.paymentType=paymentType;
        this.unitBought=unitBought;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public int getUnitBought() {
        return unitBought;
    }
}
