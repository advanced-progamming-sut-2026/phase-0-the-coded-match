package models;

public abstract class ShopItem {
    private String name;
    private int price;
    private String paymentType;
    private int unitBought;
    private String unitType;

    public ShopItem(String name, int price, String paymentType, int unitBought, String unitType){
        this.name = name;
        this.price = price;
        this.paymentType = paymentType;
        this.unitType = unitType;
        this.unitBought = unitBought;
    }
}
