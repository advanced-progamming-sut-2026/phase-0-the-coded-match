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
        this.is_locked = locked;
    }

    public boolean isReady() {
        if (!"GROWING".equals(status) || planted_timestamp == null) {
            return false;
        }
        long now = System.currentTimeMillis() / 1000;
        return (now - planted_timestamp) >= (growth_duration_hours * 3600L);
    }

}
