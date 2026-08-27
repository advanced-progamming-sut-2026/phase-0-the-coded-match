package PvZ2.APproject.views.actors;

import PvZ2.APproject.models.GameMapRelated.Tile;
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

    public TileActor(Tile tile, PlayScreen screen) {
        this.tile = tile;
        this.screen = screen;

        float pixelX = PlayScreen.BOARD_X + (tile.getRow() - 1) * PlayScreen.TILE_WIDTH;
        float pixelY = PlayScreen.BOARD_Y + (tile.getColumn() - 1) * PlayScreen.TILE_HEIGHT;

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
                    //todo: call harvest method here
                }

                return true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                inTile = false;
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        updateColor();

//        shapeRenderer.rect(PlayScreen.BOARD_X + (tile.getRow() - 1) * PlayScreen.TILE_WIDTH,
//            PlayScreen.BOARD_Y + (tile.getColumn() - 1) * PlayScreen.TILE_HEIGHT, PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);
        shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();

        super.draw(batch, parentAlpha);
    }

    public void updateColor() {
        if (inTile) {
            shapeRenderer.setColor(1, 1, 1, 0.4f);
        } else {
            shapeRenderer.setColor(1, 1, 1, 0f);
        }
    }

    public Tile getTile() {
        return tile;
    }
}
