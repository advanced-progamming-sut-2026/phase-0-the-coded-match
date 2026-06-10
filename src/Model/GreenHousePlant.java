package Model;

public class GreenHousePlant {
    private String plantName;
    private long lastHarvestTime;
    private int productionAmount;
    private int productionIntervalSeconds;

    public GreenHousePlant(String plantName, int productionAmount, int productionIntervalSeconds) {
        this.plantName = plantName;
        this.productionAmount = productionAmount;
        this.productionIntervalSeconds = productionIntervalSeconds;
        this.lastHarvestTime = System.currentTimeMillis();
    }

    public int calculateProducedAmount() {
        long now = System.currentTimeMillis();
        long elapsedSeconds = (now - lastHarvestTime) / 1000;

        if (productionIntervalSeconds <= 0) {
            return 0;
        }

        int cycles = (int) (elapsedSeconds / productionIntervalSeconds);
        return cycles * productionAmount;
    }

    public int harvest() {
        int producedAmount = calculateProducedAmount();

        if (producedAmount > 0) {
            lastHarvestTime = System.currentTimeMillis();
        }

        return producedAmount;
    }

    public String getPlantName() {
        return plantName;
    }

    public long getLastHarvestTime() {
        return lastHarvestTime;
    }

    public int getProductionAmount() {
        return productionAmount;
    }

    public int getProductionIntervalSeconds() {
        return productionIntervalSeconds;
    }
}