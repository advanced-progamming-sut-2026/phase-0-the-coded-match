package PvZ2.APproject.models;

public class DroppedSeedPacket extends SeedPacket{
    private final int x;
    private final int y;
    private int remainingTicks;
    public DroppedSeedPacket(String plantType, int x, int y, int durationTicks) {
        super(false, plantType);
        this.x = x;
        this.y = y;
        this.remainingTicks = durationTicks;
    }

    public void updateTick() {
        if (remainingTicks > 0) {
            remainingTicks--;
        }
    }

    public boolean isExpired() {
        return remainingTicks <= 0;
    }

    public String getPlantType(){
        return this.plantType;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
