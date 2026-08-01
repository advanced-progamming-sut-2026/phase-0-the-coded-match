package controllers;

import models.App;

import java.util.List;

public class GreenHouseController {
    public void plantSeed(int x, int y, List<String> unlockedPlants) {
        App.getCurrentUser().getGreenHouse().plantPot(x, y, unlockedPlants);
    }

    public void collectPlant(int x, int y) {
        App.getCurrentUser().getGreenHouse().collect(x, y);
    }

    public boolean unlockPot(int x, int y) {
        return App.getCurrentUser().getGreenHouse().unlockPot(x, y);
    }

    public void growPlant(int x, int y) {
        App.getCurrentUser().getGreenHouse().grow(x, y);
    }
}
