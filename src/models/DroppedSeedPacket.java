package models;

import models.plants.Plant;

public class DroppedSeedPacket {
    private int row;
    private int column;
    private int expirationTicksRemaining;
    private boolean isExpired;
    private Plant plant;

    public DroppedSeedPacket( int row, int col, int durationTicks) {}

    public DroppedSeedPacket(boolean isExpired, Plant plant) {
        this.isExpired = isExpired;
        this.plant = plant;
    }

    public void updateExpirationTick() {}
    public boolean hasExpired() { return false; }
    public DroppedSeedPacket claim() { return null; }
}
