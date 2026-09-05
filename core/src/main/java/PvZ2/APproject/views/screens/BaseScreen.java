package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.audio.MusicManager;
import PvZ2.APproject.controllers.menus.GameMenuController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.GameSettings;
import PvZ2.APproject.models.App;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;
import pvz.skin.PvzSkin;

public abstract class BaseScreen implements Screen {
    protected Skin skin;
    OrthographicCamera camera = new OrthographicCamera();
    protected Viewport viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
    protected Stage stage;
    protected TextureBank textures;
    protected PamPlayer player;
    protected TextureRegion background;
    protected Image backgroundImage;
    protected Texture externalBackgroundTexture;
    protected static final float VIRTUAL_WIDTH = 1280;
    protected static final float VIRTUAL_HEIGHT = 720;
    protected GameSettings gameSettings;
    protected Table currencyTable;
    protected Label coinLabel;
    protected Label gemLabel;

    @Override
    public void show() {
        skin = PvzSkin.get();
        addDialogStyleToSkin();
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        textures = ((Main) Gdx.app.getApplicationListener()).getTextures();
        player = ((Main) Gdx.app.getApplicationListener()).getPlayer();
        gameSettings = GameSettings.getInstance();
        MusicManager.playForScreen(this);
    }

    protected void addBackground(String regionName) {
        background = textures.region(regionName);
        backgroundImage = new Image(new TextureRegionDrawable(background));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
    }

    protected void addMainBackground() {
        addBackground("IMAGE_MAINMENU_BACKGROUND");
    }

    protected void addAssetBackground(String path) {
        if (externalBackgroundTexture != null) externalBackgroundTexture.dispose();
        externalBackgroundTexture = new Texture(Gdx.files.internal(path));
        backgroundImage = new Image(externalBackgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
    }

    protected void addCurrencyBar() {
        if (App.getCurrentUser() == null) {
            return;
        }

        if (currencyTable != null) {
            currencyTable.remove();
        }

        currencyTable = new Table(skin);

        Table coinTable = new Table(skin);
        Table gemTable = new Table(skin);

        Image gemImage = new Image(textures.region("IMAGE_UI_HUD_INGAME_GEM"));
        Image coinImage = new Image(textures.region("IMAGE_UI_HUD_INGAME_COIN"));
        gemLabel = new Label(Integer.toString(App.getCurrentUser().getGemsCount()), skin, "default");
        coinLabel = new Label(Integer.toString(App.getCurrentUser().getCoinsCount()), skin, "default");

        coinTable.add(coinImage).size(36, 36).padRight(4);
        coinTable.add(coinLabel).padRight(8);
        gemTable.add(gemImage).size(36, 36).padRight(4);
        gemTable.add(gemLabel).padRight(8);

        if (gameSettings.isDebugMode()) {
            TextButton cheatAddCoin = new TextButton("+", skin, "default");
            TextField coinAmount = new TextField("", skin, "default");

            TextButton cheatAddGem = new TextButton("+", skin, "default");
            TextField gemAmount = new TextField("", skin, "default");

//            coinAmount.setVisible(false);
//            gemAmount.setVisible(false);

            coinTable.add(cheatAddCoin).size(25, 25);
            coinTable.add(coinAmount).size(50, 25).padLeft(4);
            gemTable.add(cheatAddGem).size(25, 25);
            gemTable.add(gemAmount).size(50, 25).padLeft(4);

            coinAmount.setVisible(false);
            gemAmount.setVisible(false);

            cheatAddCoin.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    cheatAddCoin.setVisible(false);
                    coinAmount.setVisible(true);
                    stage.setKeyboardFocus(coinAmount);
                }
            });

            coinAmount.addListener(new InputListener() {
                @Override
                public boolean keyDown(InputEvent event, int keycode) {
                    if (keycode == Input.Keys.ENTER) {
                        try {
                            int amount = Integer.parseInt(coinAmount.getText());

                            GameMenuController.cheatAddCoinOrGem(amount, "coin");

                            coinLabel.setText(Integer.toString(App.getCurrentUser().getCoinsCount()));

                            coinAmount.setText("");
                            coinAmount.setVisible(false);
                            cheatAddCoin.setVisible(true);

                            stage.setKeyboardFocus(null);

                        } catch (NumberFormatException e) {
                            coinAmount.setText("");
                        }
                        return true;
                    }
                    return false;
                }
            });

            cheatAddGem.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    cheatAddGem.setVisible(false);
                    gemAmount.setVisible(true);
                    stage.setKeyboardFocus(gemAmount);
                }
            });

            gemAmount.addListener(new InputListener() {
                @Override
                public boolean keyDown(InputEvent event, int keycode) {
                    if (keycode == Input.Keys.ENTER) {
                        try {
                            int amount = Integer.parseInt(gemAmount.getText());

                            GameMenuController.cheatAddCoinOrGem(amount, "gem");

                            gemLabel.setText(Integer.toString(App.getCurrentUser().getGemsCount()));

                            gemAmount.setText("");
                            gemAmount.setVisible(false);
                            cheatAddGem.setVisible(true);

                            stage.setKeyboardFocus(null);

                        } catch (NumberFormatException e) {
                            gemAmount.setText("");
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

        currencyTable.add(coinTable).padRight(15);
        currencyTable.add(gemTable);
        currencyTable.pack();
        currencyTable.setPosition(
            VIRTUAL_WIDTH - currencyTable.getWidth() - 20,
            VIRTUAL_HEIGHT - currencyTable.getHeight() - 18
        );
        stage.addActor(currencyTable);
    }

    protected void updateCurrency() {
        if (App.getCurrentUser() == null || coinLabel == null || gemLabel == null) return;
        coinLabel.setText(Integer.toString(App.getCurrentUser().getCoinsCount()));
        gemLabel.setText(Integer.toString(App.getCurrentUser().getGemsCount()));
    }

    protected ImageButton addBackButton(Runnable action) {
        ImageButton backButton = new ImageButton(skin, "generic_close_circle");
        backButton.setPosition(18, VIRTUAL_HEIGHT - 68);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        stage.addActor(backButton);
        return backButton;
    }

    protected Label addMessageLabel() {
        Label label = new Label("", skin, "promo_ribbon");
        label.setVisible(false);
        label.setPosition(260, 28);
        stage.addActor(label);
        return label;
    }

    protected void showMessage(Label label, String message) {
        if (label == null) {
            return;
        }
        label.clearActions();
        label.setText(message == null ? "" : message);
        label.pack();
        label.setPosition((VIRTUAL_WIDTH - label.getWidth()) / 2f, 28);
        label.setVisible(true);
        label.getColor().a = 1f;
        label.addAction(Actions.sequence(
            Actions.delay(2f),
            Actions.fadeOut(0.4f),
            Actions.hide()
        ));
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
//        }    //TODO: fix this later for the settings requirement!! i think it should be in gamescreen not here

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
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        if (externalBackgroundTexture != null) {
            externalBackgroundTexture.dispose();
            externalBackgroundTexture = null;
        }
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        if (externalBackgroundTexture != null) {
            externalBackgroundTexture.dispose();
            externalBackgroundTexture = null;
        }
    }
}
