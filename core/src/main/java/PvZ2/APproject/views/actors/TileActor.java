package PvZ2.APproject.views.actors;

import PvZ2.APproject.models.GameMapRelated.Tile;
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
