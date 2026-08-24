package PvZ2.APproject.views.actors;

import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.GameSettings;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;

public class TileActor extends Group {
    private Tile tile;

    public TileActor(Tile tile) {
        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }
}
