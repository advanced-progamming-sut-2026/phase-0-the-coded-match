package PvZ2.APproject.views.actors;

import PvZ2.APproject.controllers.PlantController;
import PvZ2.APproject.controllers.PlantSelectionController;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.views.screens.PlayScreen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class TileActor extends Group {
    private final Tile tile;
    private final PlayScreen screen;
    private final PlantSelectionController selectionController;
    private boolean inTile;

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
                inTile = true;
                if (!screen.getHarvestMode() && selectionController.hasSelectedPlant()) {
                    selectionController.setHoveredTile(tile);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                inTile = false;
                if (selectionController.getHoveredTile() == tile) {
                    selectionController.setHoveredTile(null);
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return screen.getHarvestMode() || selectionController.hasSelectedPlant() || screen.isMiniGameTileInteractive();
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (screen.getHarvestMode()) {
                    String error = PlantController.removePlantAt(tile.getColumn(), tile.getRow());
                    if (error != null) {
                        screen.showMessage(error);
                    }
                    return;
                }
                if (screen.isMiniGameTileInteractive()) {
                    screen.handleMiniGameTileClick(tile);
                    return;
                }
                if (!selectionController.hasSelectedPlant()) return;
                selectionController.setHoveredTile(tile);
                String error = selectionController.tryPlaceSelectedPlant();
                if (error != null) {
                    screen.showMessage(error);
                }
            }
        });
    }

    public Tile getTile() {
        return tile;
    }

    public void setInTile(Boolean inTile) {
        this.inTile = inTile;
    }

    public boolean isInTile() {
        return inTile;
    }
}
