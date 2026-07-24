package models;

import controllers.ShopController;
import models.plants.Plant;
import models.plants.PlantData;

public class Shop {
    private boolean dailyItemSoldOut = false;
    private PlantData randomSeedPack;
    private PlantData randomSpecialSeedPack;
    private PlantData seedPackByChoice;
    private ShopController controller;

    public Shop(){
        this.controller = new ShopController(this);
        initializeRandomPlants();
    }

    private void checkAndRefreshDailyOffer(){
        //todo
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

    public PlantData getRandomSeedPack() {
        return randomSeedPack;
    }

    public void setRandomSeedPack(PlantData randomSeedPack) {
        this.randomSeedPack = randomSeedPack;
    }

    public PlantData getRandomSpecialSeedPack() {
        return randomSpecialSeedPack;
    }

    public void setRandomSpecialSeedPack(PlantData randomSpecialSeedPack) {
        this.randomSpecialSeedPack = randomSpecialSeedPack;
    }

    public PlantData getSeedPackByChoice() {
        return seedPackByChoice;
    }

    public void setSeedPackByChoice(PlantData seedPackByChoice) {
        this.seedPackByChoice = seedPackByChoice;
    }
}
