package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.models.GameSettings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;
import pvz.skin.PvzSkin;

import javax.swing.*;

public abstract class BaseScreen implements Screen {
    protected Skin skin;
    protected Viewport viewport;
    protected Stage stage;
    protected TextureBank textures;
    protected PamPlayer player;
    protected TextureRegion background;
    protected Image backgroundImage;
    protected static final float VIRTUAL_WIDTH = 1024;
    protected static final float VIRTUAL_HEIGHT = 768;

    @Override
    public void show() {
        skin = PvzSkin.get();
        addDialogStyleToSkin();
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        textures = ((Main) Gdx.app.getApplicationListener()).getTextures();
        player = ((Main) Gdx.app.getApplicationListener()).getPlayer();
    }

    @Override
    public void render(float delta) {
        textures.update();

        ScreenUtils.clear(0, 0, 0, 1);

//        if (GameSettings.getInstance().isShowGrid()) {
//            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//            shapeRenderer.setColor(Color.RED);
//
//            // Draw grid overlay across lawn rows (5) and columns (9)
//            for (int col = 0; col <= 9; col++) {
//                shapeRenderer.line(GRID_START_X + col * CELL_WIDTH, GRID_START_Y,
//                    GRID_START_X + col * CELL_WIDTH, GRID_START_Y + (5 * CELL_HEIGHT));
//            }
//            for (int row = 0; row <= 5; row++) {
//                shapeRenderer.line(GRID_START_X, GRID_START_Y + row * CELL_HEIGHT,
//                    GRID_START_X + (9 * CELL_WIDTH), GRID_START_Y + row * CELL_HEIGHT);
//            }
//            shapeRenderer.end();
//        }    //TODO: fix this later for the settings requirement!!

        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);

        stage.act(delta);
        stage.draw();
    }

    private void addDialogStyleToSkin(){
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = skin.getFont("ASHLEYSCRIPTMTSTD");
        windowStyle.titleFontColor = skin.getColor("White");
        skin.add("default", windowStyle);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
