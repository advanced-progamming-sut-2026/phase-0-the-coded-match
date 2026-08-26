package PvZ2.APproject.views;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.PlantSelectionController;
import PvZ2.APproject.models.GameMapRelated.GameMap;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.GameSettings;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.views.actors.TileActor;
import PvZ2.APproject.views.screens.PlayScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import pvz.libpvz.textures.TextureBank;

public class GameMapView extends Group {
    private final Main game;
    private final PlayScreen gameScreen;
    private final PlantSelectionController plantSelectionController;
    private Level currentLevel;
    private TextureBank textures;
    private TextureRegion background;
    private ShapeRenderer shapeRenderer;

    public GameMapView(Main game, Level currentLevel, TextureBank textures, PlayScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
        this.plantSelectionController = gameScreen.getPlantSelectionController();
        this.currentLevel = currentLevel;
        this.textures = textures;
        shapeRenderer = new ShapeRenderer();

        loadBackground();
        createTiles();
    }

    public void loadBackground() {
        if (getPath() != null) {
        background = textures.region(getPath());
        }
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

                TileActor tileActor = new TileActor(tile, plantSelectionController);

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

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        Tile hoveredTile = plantSelectionController.getHoveredTile();
        if (hoveredTile != null && plantSelectionController.hasSelectedPlant()) {
            drawPlacementHighlight(batch, hoveredTile);
        }

        if (GameSettings.getInstance().isShowGrid()) {
            batch.end();

            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);

            int startX = 290;
            int startY = 130;
            int spacingX = 80;
            int spacingY = 100;

            GameMap gameMap = currentLevel.getGameMap();

            for (int i = 1; i < gameMap.getRows(); i++) {
                for (int j = 1; j < gameMap.getColumns(); j++) {
                    shapeRenderer.rect(
                        startX + (j - 1) * spacingX,
                        startY + (i - 1) * spacingY,
                        spacingX,
                        spacingY
                    );
                }
            }

            shapeRenderer.end();
            batch.begin();
        }
    }

    private void drawPlacementHighlight(Batch batch, Tile tile){
        batch.end();

        shapeRenderer.setProjectionMatrix(
            batch.getProjectionMatrix()
        );

        shapeRenderer.begin(
            ShapeRenderer.ShapeType.Line
        );

        shapeRenderer.setColor(Color.WHITE);

        int startX = 290;
        int startY = 130;
        int spacingX = 80;
        int spacingY = 100;

        int row = tile.getRow();
        int column = tile.getColumn();

        GameMap gameMap = currentLevel.getGameMap();

        for (int j = 1; j < gameMap.getColumns(); j++) {
            float x = startX + (j - 1) * spacingX;
            float y = startY + (row - 1) * spacingY;
            shapeRenderer.rect(
                x,
                y,
                spacingX,
                spacingY
            );
        }

        for (int i = 1; i < gameMap.getRows(); i++) {
            float x = startX + (column - 1) * spacingX;
            float y = startY + (i - 1) * spacingY;
            shapeRenderer.rect(
                x,
                y,
                spacingX,
                spacingY
            );
        }

        shapeRenderer.end();
        batch.begin();
    }
}
