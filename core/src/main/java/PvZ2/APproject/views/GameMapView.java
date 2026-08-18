package PvZ2.APproject.views;

import PvZ2.APproject.Main;
import PvZ2.APproject.models.GameMapRelated.GameMap;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.views.actors.TileActor;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import pvz.libpvz.textures.TextureBank;

public class GameMapView extends Group {
    private final Main game;
    private Level currentLevel;
    private TextureBank textures;
    private TextureRegion background;

    public GameMapView(Main game, Level currentLevel, TextureBank textures) {
        this.game = game;
        this.currentLevel = currentLevel;
        this.textures = textures;

        loadBackground();
        createTiles();
    }

//    public GameMapView(Main game, TextureBank textures) {
//        this.game = game;
//        this.textures = textures;
//        loadBackground();
//    }

    public void loadBackground() {
        if (getPath() != null) {
        background = textures.region(getPath());
        }
//        background = textures.region("IMAGE_BACKGROUNDS_EGYPT_TEXTURE");
    }

    public String getPath() {
        switch (currentLevel.getCurrentSeason().getData().getId()) {
            case 1:
                return "IMAGE_BACKGROUNDS_EGYPT_TEXTURE";
            case 2:
                return "IMAGE_BACKGROUNDS_ICEAGE_TEXTURE";
            case 3:
                return "IMAGE_BACKGROUNDS_BEACH_TEXTURE";
            case 4:
                return "IMAGE_BACKGROUNDS_DARK_TEXTURE";
        }
        return "";
    }

    public void createTiles() {
        GameMap gameMap = currentLevel.getGameMap();

        int startX = 290;
        int startY = 130;
        int spacingX = 80;
        int spacingY = 100;

        for (int i = 1; i < gameMap.getRows(); i++) {
            for (int j = 1; j < gameMap.getColumns(); j++) {
                Tile tile = gameMap.getTile(j, i);

                TileActor tileActor = new TileActor(tile);

                tileActor.setPosition(startX, startY);

                addActor(tileActor);

                startX += spacingX;
            }
            startX = 290;
            startY += spacingY;
        }
    }

    public TextureRegion getBackground() {
        return background;
    }
}
