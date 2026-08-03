package models.greenhouse;

public class GreenHousePot {
    public int x;
    public int y;
    public boolean is_locked;
    public String status;
    public String plant_type;
    public Long planted_timestamp;
    public int growth_duration_hours;

    public GreenHousePot(int x, int y, boolean locked){
        this.x = x;
        this.y = y;
        is_locked = locked;
        status = "EMPTY";
    }

    public void ensureDefaults() { if (status == null) status = plant_type == null ? "EMPTY" : "GROWING"; }

    public boolean isReady() {
        ensureDefaults();
        if (!"GROWING".equals(status) || planted_timestamp == null) return false;
        return System.currentTimeMillis() / 1000 - planted_timestamp >= growth_duration_hours * 3600L;
    }
}
