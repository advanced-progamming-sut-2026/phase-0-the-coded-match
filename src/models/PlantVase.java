package models;

import enums.VaseContent;

public class PlantVase extends Vase{
    public PlantVase(String vaseType, VaseContent contains) {
        super(vaseType, contains);
    }

    @Override
    public void breakVase() {

    }


}
