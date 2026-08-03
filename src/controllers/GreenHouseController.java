package controllers;

import models.App;

public class GreenHouseController {
    public static void plantSeed(int x, int y) {
        App.getCurrentUser().getGreenHouse().plantPot(x, y, App.getCurrentUser().getCollection().getAvailablePlantsIds());
    }

    public static void collectPlant(int x, int y) {
        App.getCurrentUser().getGreenHouse().collect(x, y);
    }

    public static void unlockPot(int count) {
        App.getCurrentUser().getGreenHouse().unlockPots(count);
    }

    public static void growPlant(int x, int y) {
        App.getCurrentUser().getGreenHouse().grow(x, y);
    }
}
