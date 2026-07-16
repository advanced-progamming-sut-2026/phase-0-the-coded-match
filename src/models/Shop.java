package models;

import controllers.ShopController;
import models.plants.Plant;

public class Shop {
    private boolean dailyItemSoldOut = false;
    private Plant randomSeedPack;
    private Plant randomSpecialSeedPack;
    private Plant seedPackByChoice;
    private ShopController controller;

    public Shop(){
        this.controller = new ShopController(this);
        initializeRandomPlants();
    }



    private void checkAndRefreshDailyOffer(){

    }

    private void initializeRandomPlants(){
        randomSeedPack = controller.getRandomPlant();
        randomSpecialSeedPack = controller.getRandomPlant();
    }

    public boolean isDailyItemSoldOut() {
        return dailyItemSoldOut;
    }

    public void setDailyItemSoldOut(boolean dailyItemSoldOut) {
        this.dailyItemSoldOut = dailyItemSoldOut;
    }

    public Plant getRandomSeedPack() {
        return randomSeedPack;
    }

    public void setRandomSeedPack(Plant randomSeedPack) {
        this.randomSeedPack = randomSeedPack;
    }

    public Plant getRandomSpecialSeedPack() {
        return randomSpecialSeedPack;
    }

    public void setRandomSpecialSeedPack(Plant randomSpecialSeedPack) {
        this.randomSpecialSeedPack = randomSpecialSeedPack;
    }

    public Plant getSeedPackByChoice() {
        return seedPackByChoice;
    }

    public void setSeedPackByChoice(Plant seedPackByChoice) {
        this.seedPackByChoice = seedPackByChoice;
    }
}
