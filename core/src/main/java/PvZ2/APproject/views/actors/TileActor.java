package PvZ2.APproject.views.actors;

import PvZ2.APproject.controllers.PlantSelectionController;
import PvZ2.APproject.models.GameMapRelated.Tile;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class TileActor extends Group {
    private Tile tile;
    private PlantSelectionController selectionController;

    public TileActor(Tile tile, PlantSelectionController plantSelectionController) {
        this.tile = tile;
        this.selectionController = plantSelectionController;

        addListener(new InputListener(){

            @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {

                if(plantSelectionController.hasSelectedPlant()){
                    plantSelectionController.setHoveredTile(tile);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {

                if (plantSelectionController.getHoveredTile() == tile) {
                    plantSelectionController.setHoveredTile(null);
                }
            }

            @Override
            public boolean touchDown( InputEvent event, float x, float y, int pointer, int button) {

                return plantSelectionController.hasSelectedPlant();
            }

            @Override
            public void touchUp( InputEvent event, float x, float y, int pointer, int button) {

                if (!plantSelectionController.hasSelectedPlant()) {
                    return;
                }

                plantSelectionController.setHoveredTile(tile);

                String error =
                    plantSelectionController.tryPlaceSelectedPlant();

                if (error != null) {
                    System.out.println(error);
                }
            }
        });
    }

    public Tile getTile() {
        return tile;
    }
}
