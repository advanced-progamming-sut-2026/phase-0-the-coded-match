package enums.ShopRelated;

import static enums.ShopRelated.PaymentType.*;
import static enums.ShopRelated.UnitType.*;

public enum ShopItemData {
    POT(1, "Pot", 2000, COIN, 1, ONE_POT),
    PLANT_FOOD(2, "Plant food", 3, GEM, 1, ONE_PLANT_FOOD),
    SEED_PACKET_BY_CHANCE(3, "Seed Packet by chance", 1000, COIN, 5, SEED_PACKETS),
    SEED_PACKET_BY_CHOICE(4, "Seed Packet by choice", 5, GEM, 10, SEED_PACKETS),
    EXCHANGE_CURRENCY(5, "Exchange currency", 5, GEM, 500, COINS),
    SEED_PACKET_DAILY(6, "Seed Packet daily", 1600, COIN, 10, SEED_PACKETS);

    private final int id;
    private final String name;
    private final int price;
    private final PaymentType paymentType;
    private final int unitBought;
    private final UnitType unitType;

    ShopItemData(int id ,String name, int price, PaymentType paymentType, int unitBought, UnitType unitType){
        this.id = id;
        this.name=name;
        this.price=price;
        this.paymentType=paymentType;
        this.unitBought=unitBought;
        this.unitType=unitType;
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

    public UnitType getUnitType() {
        return unitType;
    }
}
