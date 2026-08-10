package models;

import models.plants.Plant;

public class SeedPacket {
    private int row;
    private int column;
    private int remainingTicks;
    private boolean isExpired;
    String plantType;

    public SeedPacket(boolean isExpired, String plant) {
        this.isExpired = isExpired;
        this.plantType = plant;
    }

}
