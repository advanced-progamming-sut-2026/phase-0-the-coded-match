package PvZ2.APproject.views.actors;

import PvZ2.APproject.controllers.PlantSelectionController;
import PvZ2.APproject.models.GameMapRelated.Tile;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import PvZ2.APproject.models.GameSettings;
import PvZ2.APproject.views.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class TileActor extends Group {
    private Tile tile;
    private PlayScreen screen;
    private Boolean inTile = false;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private PlantSelectionController selectionController;

    public TileActor(Tile tile, PlayScreen screen, PlantSelectionController plantSelectionController) {
        this.tile = tile;
        this.screen = screen;
        this.selectionController = plantSelectionController;

        float pixelX = PlayScreen.BOARD_X + (tile.getColumn() - 1) * PlayScreen.TILE_WIDTH;
        float pixelY = PlayScreen.BOARD_Y + (tile.getRow() - 1) * PlayScreen.TILE_HEIGHT;

        setBounds(pixelX, pixelY, PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);

        addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!screen.getHarvestMood()) {
                    return;
                }

                inTile = true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!screen.getHarvestMood()) {
                    return false;
                }

                if (screen.getHarvestMood()) {
                    plantSelectionController.getPlantController().pluckPlant(tile);
                    //todo: remove plant animation too
                }

                return true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                inTile = false;
            }
        });

//        this.selectionController = plantSelectionController;
//
//        addListener(new InputListener(){
//
//            @Override
//            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//
//                if(plantSelectionController.hasSelectedPlant()){
//                    plantSelectionController.setHoveredTile(tile);
//                }
//            }
//
//            @Override
//            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//
//                if (plantSelectionController.getHoveredTile() == tile) {
//                    plantSelectionController.setHoveredTile(null);
//                }
//            }
//
//            @Override
//            public boolean touchDown( InputEvent event, float x, float y, int pointer, int button) {
//
//                return plantSelectionController.hasSelectedPlant();
//            }
//
//            @Override
//            public void touchUp( InputEvent event, float x, float y, int pointer, int button) {
//
//                if (!plantSelectionController.hasSelectedPlant()) {
//                    return;
//                }
//
//                plantSelectionController.setHoveredTile(tile);
//
//                String error =
//                    plantSelectionController.tryPlaceSelectedPlant();
//
//                if (error != null) {
//                    System.out.println(error);
//                }
//            }
//        });  TODO: I made these for placing the plants (planting) but when i pulled someone wrote the same methods in a
//                   different way i'll keep this here but if its useles delete it later on !!
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if (screen.getHarvestMood() && inTile) {

            batch.end();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.setTransformMatrix(batch.getTransformMatrix());

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1f, 1f, 1f, 0.5f);

            shapeRenderer.rect(
                getX(),
                getY(),
                getWidth(),
                getHeight()
            );

            shapeRenderer.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);

            batch.begin();
        }

        super.draw(batch, parentAlpha);
    }

    public Tile getTile() {
        return tile;
    }

    public void setInTile(Boolean inTile) {
        this.inTile = inTile;
    }
}
