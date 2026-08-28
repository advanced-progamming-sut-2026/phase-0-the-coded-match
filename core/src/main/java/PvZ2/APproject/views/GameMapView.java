package PvZ2.APproject.views;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.PlantSelectionController;
import PvZ2.APproject.enums.SeasonType;
import PvZ2.APproject.enums.TileType;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import pvz.libpvz.textures.TextureBank;

import java.util.HashMap;
import java.util.Map;

public class GameMapView extends Group {
    private final Main game;
    private final PlantSelectionController plantSelectionController;
    private final Map<Tile, TileActor> tileActors = new HashMap<>();
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

                /// TEST ///

                if (i == 1 && j == 5) {
                    tile.setNecromancyPotential(true);
                }

                /// END OF TEST ///

                TileActor tileActor = new TileActor(tile, screen, plantSelectionController);

                tileActor.setSize(PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);

//                tileActor.setPosition(startX, startY);

                addActor(tileActor);
                tileActors.put(tile, tileActor);

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

        drawSeasonTiles(batch);

//        Tile hoveredTile = plantSelectionController.getHoveredTile();
//        if (hoveredTile != null && plantSelectionController.hasSelectedPlant()) {
//            drawPlacementHighlight(batch, hoveredTile);
//        }  TODO: this is for highlighting the column and row of the plant wanting to be placed on the tile

        if (GameSettings.getInstance().isShowGrid()) {
            drawRedLinesOnGrid(batch);
        }

    }

    private void drawSeasonTiles(Batch batch){
        GameMap map = currentLevel.getGameMap();

        int rows = map.getRows();
        int columns = map.getColumns();

        for(int row = 1; row <= rows; row++){
            for(int col = 1; col <= columns; col++){
                Tile tile = map.getTile(col, row);

                if (tile == null) {
                    continue;
                }
                TileActor tileActor = tileActors.get(tile);
                float x = tileActor.getX();
                float y = tileActor.getY();

                if(tile.getType() == TileType.WATER){
                    batch.draw(textures.region("IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_BEACH_WATER"), x - 5 , y - 10, PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);
                    continue;
                }

                if(tile.isSlippery()){
                    batch.draw(textures.region("IMAGE_EFFECTS_ZOMBONI_TILE_ICE_ZOMBONI_TILE_ICE_133X157"), x - 5 , y - 10, PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);
                    continue;
                }

                if(tile.holdsNecromancyPotential()){
                    batch.draw(textures.region("IMAGE_NPC_GHOSTPEPPER_GHOSTPEPPER_277X309"), x-5, y-10, 50, 70);
                } // TODO : when it holds the necromancy potential maybe it should show on the tile otherwise theres no need

                if(tile.isGrave() && tile.getType() == TileType.GRAVE){
                    if(tile.holdsNecromancyPotential()){
                        batch.draw(textures.region("IMAGE_GRAVESTONES_TUTORIAL_GRAVESTONE_TUTORIAL_GRAVESTONE_101X159"), x, y, PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);
                        continue;
                    }
                    switch(tile.getGraveReward()){
                        case NONE:
                            if(currentLevel.getCurrentSeason().getData().getId() == 1){
                                batch.draw(textures.region("IMAGE_GRAVESTONES_EGYPT_HIEROGLYPH_EGYPT_HIEROGLYPH_118X148"), x, y, PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);
                            }else {
                                batch.draw(textures.region("IMAGE_GRAVESTONES_DARK_NOOP_DARK_NOOP_132X160"), x, y, PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);
                            }
                            break;
                        case PLANT_FOOD:
                            batch.draw(textures.region("IMAGE_GRAVESTONES_DARK_PLANTFOOD_DARK_PLANTFOOD_132X160"), x, y, PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);
                            break;
                        case SUN_50:
                            batch.draw(textures.region("IMAGE_GRAVESTONES_DARK_SUN_DARK_SUN_132X160"), x, y, PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);
                            break;
                        default:
                            batch.draw(textures.region("IMAGE_GRAVESTONES_DARK_NOOP_DARK_NOOP_132X160"), x, y, PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);
                    }
                }

            }
        }
    }

    private void drawRedLinesOnGrid(Batch batch){
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
