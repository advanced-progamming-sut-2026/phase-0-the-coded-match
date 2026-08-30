package PvZ2.APproject.views.actors;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.controllers.PlantSelectionController;
import PvZ2.APproject.models.plants.PlantData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import pvz.libpvz.textures.TextureBank;

public class PlantBox extends Group {
    private final PlantData plantData;
    private final PlantSelectionController selectionController;
    private TextureRegion background;
    private TextureRegion plantImage;
    private Skin skin;

    public PlantBox(PlantData plantDatal, PlantSelectionController selectionController, TextureBank textures, Skin skin) {
        this.plantData = plantDatal;
        this.selectionController = selectionController;
        this.skin = skin;

        setSize(100, 120);
        setTouchable(Touchable.enabled);

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

        plantImage = textures.region("IMAGE_UI_PACKETS_" + plantData.getId().toUpperCase());

        Label sunCost = new Label(Integer.toString(plantData.getSunCost()), skin, "default");

        Image sun = new Image(
            new TextureRegionDrawable(
                textures.region("IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN")
            )
        );

        sunCost.setPosition(10, 10);
        sun.setSize(20, 20);
        sun.setPosition(30, 12);
        addActor(sunCost);
        addActor(sun);

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

        super.draw(batch, parentAlpha);

    }


}
