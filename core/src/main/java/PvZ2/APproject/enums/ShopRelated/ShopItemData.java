package PvZ2.APproject.enums.ShopRelated;

import static PvZ2.APproject.enums.ShopRelated.PaymentType.*;

public enum ShopItemData {
    POT(1, "Pot", 2000, COIN, 1, "IMAGE_ZEN_GARDEN_GROWING_PLANT_SLOT_GROWING_PLANT_SLOT_184X161_2"),
    PLANT_FOOD(2, "Plant food", 3, GEM, 1, "IMAGE_UI_HUD_INGAME_PLANTFOOD_BUTTON_DOWN"),
    SEED_PACKET_BY_CHANCE(3, "Seed Packet by chance", 1000, COIN, 5,"IMAGE_UI_STOREMULTI_SEEDPACKETICON"),
    SEED_PACKET_BY_CHOICE(4, "Seed Packet by choice", 5, GEM, 10,  "IMAGE_UI_MAINMENU_FEATURE_UNLOCK_CHARACTERS_R"),
//     "IMAGE_UI_CHOOSER_SUGGEST_A_PLANT_POPUP_BG"
    EXCHANGE_CURRENCY(5, "Exchange currency", 5, GEM, 500, "IMAGE_UI_COINS_STACK_6"),
    SEED_PACKET_DAILY(6, "Seed Packet daily", 1600, COIN, 10, "IMAGE_UI_QUESTS_QUESTICONS_PLANT");

    private final int id;
    private final String name;
    private final int price;
    private final PaymentType paymentType;
    private final int unitBought;
    private final String imagePath;

    ShopItemData(int id ,String name, int price, PaymentType paymentType, int unitBought, String imagePath){
        this.id = id;
        this.name=name;
        this.price=price;
        this.paymentType=paymentType;
        this.unitBought=unitBought;
        this.imagePath=imagePath;
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

    public String getImagePath(){return  imagePath;}
}
