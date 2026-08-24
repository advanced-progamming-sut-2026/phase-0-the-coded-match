package PvZ2.APproject.views.actors;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.controllers.PlantSelectionController;
import PvZ2.APproject.models.plants.PlantData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import pvz.libpvz.textures.TextureBank;

public class PlantBox extends Group {
     private final PlantData plantData;
     private final PlantSelectionController selectionController;
    private TextureRegion background;
    private TextureRegion plantImage;


    public PlantBox(PlantData plantDatal, PlantSelectionController selectionController, TextureBank textures) {
        this.plantData = plantDatal;
        this.selectionController = selectionController;

        setSize(100, 120);

        createVisuals(textures);
        addClickListener();
    }

    public void createVisuals(TextureBank textures){
        switch(GameManagerController.getInstance().getCurrentLevel().getCurrentSeason().getName()) {
            case "ANCIENT EGYPT":
                background = textures.region("IMAGE_UI_PACKETS_EGYPT");
                break;
            case "FROSTBITE CAVES":
                background = textures.region("IMAGE_UI_PACKETS_ICEAGE");
                break;
            case "BIG BEACH WAVES":
                background = textures.region("IMAGE_UI_PACKETS_BEACH");
                break;
            case "DARK AGES":
                background = textures.region("IMAGE_UI_PACKETS_DARK");
                break;
            default:
                background = textures.region("IMAGE_UI_PACKETS_PIRATE");
        }

        plantImage = textures.region("IMAGE_UI_PACKETS_" + plantData.getName().toUpperCase());

    }

    private void addClickListener() {

        addListener(new ClickListener() {

            @Override
            public void clicked(
                InputEvent event,
                float x,
                float y
            ) {

                selectionController.selectPlant(plantData);
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        batch.draw(
            background,
            getX(),
            getY(),
            getWidth(),
            getHeight()
        );

        batch.draw(
            plantImage,
            getX() + 20,
            getY() + 35,
            60,
            60
        );

    }


}
