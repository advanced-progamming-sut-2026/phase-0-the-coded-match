package enums;

import static enums.PaymentType.*;
import static enums.UnitType.*;

enum PaymentType{
    COIN,
    GEM
}

enum UnitType{
    ONE_POT,
    ONE_PLANT_FOOD,
    SEED_PACKETS,
    COINS
}

public enum ShopItemData {
    POT("Pot", 2000, COIN, 1, ONE_POT, 1000000000 ),
    PLANT_FOOD("Plant food", 3, GEM, 1, ONE_PLANT_FOOD, 1000000000),
    SEED_PACKET_BY_CHANCE("Seed Packet by chance", 1000, COIN, 5, SEED_PACKETS, 1000000000),
    SEED_PACKET_BY_CHOICE("Seed Packet by choice", 5, GEM, 10, SEED_PACKETS, 1000000000),
    EXCHANGE_CURRENCY("Exchange currency", 5, GEM, 500, COINS, 1000000000),
    SEED_PACKET_DAILY("Seed Packet daily", 2000, COIN, 10, SEED_PACKETS, 1);

    private final String name;
    private final int price;
    private final PaymentType paymentType;
    private final int unitBought;
    private final UnitType unitType;
    private final long MaxInADaay;

    ShopItemData(String name, int price, PaymentType paymentType, int unitBought, UnitType unitType, int MaxInAday){
        this.name=name;
        this.price=price;
        this.paymentType=paymentType;
        this.unitBought=unitBought;
        this.unitType=unitType;
        this.MaxInADaay=MaxInAday;
    }


}
