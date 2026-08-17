package PvZ2.APproject.models;

import PvZ2.APproject.models.plants.PlantData;

import java.time.LocalDate;

public class Shop {
    private boolean dailyItemSoldOut = false;
    private PlantData randomSeedPack;
    private PlantData randomSpecialSeedPack;
    private PlantData seedPackByChoice;
    private String lastUpdateDate;

    public Shop(){}

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

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
