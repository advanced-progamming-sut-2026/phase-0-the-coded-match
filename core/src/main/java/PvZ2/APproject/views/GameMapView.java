package PvZ2.APproject.views;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.PlantSelectionController;
import PvZ2.APproject.models.GameMapRelated.GameMap;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.GameSettings;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.views.actors.TileActor;
import PvZ2.APproject.views.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import pvz.libpvz.textures.TextureBank;

public class GameMapView extends Group {
    private final Main game;
    private final PlantSelectionController plantSelectionController;
    private PlayScreen screen;
    private Level currentLevel;
    private TextureBank textures;
    private TextureRegion background;
    private ShapeRenderer shapeRenderer;
    private Image backgroundImage;
    private Stage stage;

    public GameMapView(Main game, PlayScreen screen, Level currentLevel, TextureBank textures, Image backgroundImage, Stage stage) {
        this.game = game;
        this.screen = screen;
        this.plantSelectionController = screen.getPlantSelectionController();
        this.currentLevel = currentLevel;
        this.textures = textures;
        shapeRenderer = new ShapeRenderer();
        this.backgroundImage = backgroundImage;
        this.stage = stage;

        loadBackground();
        createTiles();
    }

    public void loadBackground() {
        if (getPath() != null) {
            background = textures.region(getPath());
//            backgroundImage = new Image(new TextureRegionDrawable(getBackground()));
//            backgroundImage.setFillParent(true);
//            stage.addActor(backgroundImage);
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

        int startX = 260;
        int startY = 80;
        int spacingX = 80;
        int spacingY = 100;

        for (int i = 1; i <= gameMap.getRows(); i++) {
            for (int j = 1; j <= gameMap.getColumns(); j++) {
                Tile tile = gameMap.getTile(j, i);

                TileActor tileActor = new TileActor(tile, screen, plantSelectionController);

                tileActor.setSize(PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);

//                tileActor.setPosition(startX, startY);

                addActor(tileActor);

                startX += spacingX;
            }
            startX = 260;
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
            shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);

            int startX = 260;
            int startY = 80;
            int spacingX = 80;
            int spacingY = 100;

            GameMap gameMap = currentLevel.getGameMap();

            for (int i = 1; i <= gameMap.getRows(); i++) {
                for (int j = 1; j <= gameMap.getColumns(); j++) {
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

    public void updateTile() {
        Vector2 mouse = new Vector2(
            Gdx.input.getX(),
            Gdx.input.getY()
        );

        stage.getViewport().unproject(mouse);

        for (Actor actor : getChildren()) {

            if (!(actor instanceof TileActor)) {
                continue;
            }

            TileActor tileActor = (TileActor) actor;

            boolean inTile =
                mouse.x >= tileActor.getX()
                    && mouse.x <= tileActor.getX() + tileActor.getWidth()
                    && mouse.y >= tileActor.getY()
                    && mouse.y <= tileActor.getY() + tileActor.getHeight();

            tileActor.setInTile(inTile);
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

        for (int j = 1; j <= gameMap.getColumns(); j++) {
            float x = startX + (j - 1) * spacingX;
            float y = startY + (row - 1) * spacingY;
            shapeRenderer.rect(
                x,
                y,
                spacingX,
                spacingY
            );
        }

        for (int i = 1; i <= gameMap.getRows(); i++) {
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
