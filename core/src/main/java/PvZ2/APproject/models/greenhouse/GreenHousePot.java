package PvZ2.APproject.models.greenhouse;

public class GreenHousePot {
    public int x;
    public int y;
    public boolean isLocked;
    public String status;
    public String plantType;
    public Long plantedTimestamp;
    public int growthDurationHours;

    public GreenHousePot(int x, int y, boolean locked) {
        this.x = x;
        this.y = y;
        isLocked = locked;
        status = "EMPTY";
    }

    public void ensureDefaults() {
        if (status == null) {
            status = plantType == null ? "EMPTY" : "GROWING";
        }
    }

    public boolean isReady() {
        ensureDefaults();
        return getRemainingTime() == 0 && "GROWING".equals(status);
    }

    public String getPlantType() {
        return plantType;
    }

    public long getRemainingTime() {
        if (!"GROWING".equals(status) || plantedTimestamp == null) {
            return 0;
        }

        long now = System.currentTimeMillis() / 1000;
        long totalSeconds = growthDurationHours * 3600L;
        long elapsedSeconds = now - plantedTimestamp;

        return Math.max(0, totalSeconds - elapsedSeconds);
    }

    public int getGrowCost() {
        if (getRemainingTime() <= 0) {
            return 0;
        }

        return (int) Math.ceil(getRemainingTime() / 3600.0);
    }
}
