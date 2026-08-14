package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import pvz.libpvz.textures.TextureBank;
import pvz.skin.PvzSkin;

import javax.swing.*;

public abstract class BaseScreen implements Screen {
    protected Skin skin;
    protected Viewport viewport;
    protected Stage stage;
    protected TextureBank textures;
    protected TextureRegion background;
    protected Image backgroundImage;
    protected static final float VIRTUAL_WIDTH = 1024;
    protected static final float VIRTUAL_HEIGHT = 768;

    @Override
    public void show() {
        skin = PvzSkin.get();
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        textures = ((Main) Gdx.app.getApplicationListener()).getTextures();
    }

    @Override
    public void render(float delta) {
        textures.update();

        ScreenUtils.clear(0, 0, 0, 1);

        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);

        stage.act(delta);
        stage.draw();
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
