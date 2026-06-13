package models;

public class DroppedSeedPacket {
    private int row;
    private int column;
    private int expirationTicksRemaining;
    private boolean isExpired;

    public DroppedSeedPacket( int row, int col, int durationTicks) {}


    public void updateExpirationTick() {}
    public boolean hasExpired() { return false; }
    public DroppedSeedPacket claim() { return null; }
}
