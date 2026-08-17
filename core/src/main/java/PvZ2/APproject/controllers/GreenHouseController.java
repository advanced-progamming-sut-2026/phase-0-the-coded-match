package PvZ2.APproject.controllers;

import PvZ2.APproject.Main;
import PvZ2.APproject.models.App;

public class GreenHouseController {
    public void plantSeed(int x, int y) {
        App.getCurrentUser().getGreenHouse().plantPot(x, y, App.getCurrentUser().getCollection().getAvailablePlantsIds());
    }

    public String collectPlant(int x, int y) {
        return App.getCurrentUser().getGreenHouse().collect(x, y);
    }

    public void growPlant(int x, int y) {
        App.getCurrentUser().getGreenHouse().grow(x, y);
    }

    public void exit(Main game) {
//        game.setScreen(new ); //todo: new GameScreen
    }
}
