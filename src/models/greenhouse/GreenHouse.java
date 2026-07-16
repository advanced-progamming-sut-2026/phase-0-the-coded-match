package models.greenhouse;

public class GreenHouse {

    private GreenHousePot[][] pots = new GreenHousePot[4][5];
    private static int potsCount;

    public void plant (int x, int y, GreenHousePlant plant) {

    }

    public void collect (int x, int y) {

    }

    public void grow (int x, int y) {

    }
    public void unlockPot (int x, int y) {

    }

    public static int getPotsCount() {
        return potsCount;
    }

    public void setPotsCount(int potsCount) {
        this.potsCount = potsCount;
    }
}
