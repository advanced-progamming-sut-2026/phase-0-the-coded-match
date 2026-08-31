package PvZ2.APproject.controllers;

import PvZ2.APproject.Main;
import PvZ2.APproject.models.App;
import PvZ2.APproject.controllers.menus.SignupMenuController;

public class GreenHouseController {
    public void plantSeed(int x, int y) {
        if (App.getCurrentUser() == null || App.getCurrentUser().getGreenHouse() == null) return;
        App.getCurrentUser().getGreenHouse().plantPot(x, y, App.getCurrentUser().getCollection().getAvailablePlantsIds());
        SignupMenuController.saveToJson();
    }

    public String collectPlant(int x, int y) {
        if (App.getCurrentUser() == null || App.getCurrentUser().getGreenHouse() == null) return "No logged in user";
        String result = App.getCurrentUser().getGreenHouse().collect(x, y);
        SignupMenuController.saveToJson();
        return result;
    }

    public void growPlant(int x, int y) {
        if (App.getCurrentUser() == null || App.getCurrentUser().getGreenHouse() == null) return;
        App.getCurrentUser().getGreenHouse().grow(x, y);
        SignupMenuController.saveToJson();
    }

    public void exit(Main game) {
//        game.setScreen(new ); //todo: new GameScreen
    }
}
