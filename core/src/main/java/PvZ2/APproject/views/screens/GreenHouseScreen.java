package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.GreenHouseController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.greenhouse.GreenHousePot;
import PvZ2.APproject.views.actors.PotActor;
import PvZ2.APproject.views.menus.MainMenu;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GreenHouseScreen extends BaseScreen {
    private final Main game;
    private GreenHouseController controller;
    private Label messageNotif;

    public GreenHouseScreen(Main game) {
        this.game = game;
        this.controller = new GreenHouseController();
    }

    @Override
    public void show() {
        super.show();

        background = textures.region("IMAGE_BACKGROUNDS_ZEN_GARDEN");
        TextureRegion cropped = new TextureRegion(background, 363, 3, (int) VIRTUAL_WIDTH, (int) VIRTUAL_HEIGHT);
        backgroundImage = new Image(new TextureRegionDrawable(cropped));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        addCurrencyBar();

        createPots();

        messageNotif = addMessageLabel();

        addBackButton(() -> {
            App.setCurrentMenu(Menu.GAME_MENU);
            game.setScreen(new GameMenuScreen(game));
        });
    }

    public void createPots() {
        TextureRegion potTexture =
            textures.region("IMAGE_ZEN_GARDEN_GROWING_PLANT_SLOT_GROWING_PLANT_SLOT_184X161");

        TextureRegion lockTexture = textures.region("IMAGE_ZEN_GARDEN_LOCKED_POT_ICON");

        float startX = 200;
        float startY = 100;
        float spacingX = 170;
        float spacingY = 160;

        for (GreenHousePot[] pots : App.getCurrentUser().getGreenHouse().getGrid()) {
            for (GreenHousePot pot : pots) {
                PotActor potActor = new PotActor(pot, potTexture, lockTexture, player, skin, controller);
                potActor.setPosition(startX, startY);

                potActor.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        handlePotClick(potActor);
                    }
                });

                stage.addActor(potActor);

                startX += spacingX;
            }
            startX = 200;
            startY += spacingY;
        }
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 4; j++) {
//                PotActor potActor = new PotActor(new GreenHousePot(j, i, i == 0), potTexture, lockTexture, player, skin);
//                potActor.setPosition(startX, startY);
//
//                potActor.addListener(new ClickListener() {
//                    @Override
//                    public void clicked(InputEvent event, float x, float y) {
//                        handlePotClick(potActor);
//                    }
//                });
//
//                stage.addActor(potActor);
//
//                startX += spacingX;
//
//            }
//            startX = 200;
//            startY += spacingY;
//        }
    }

    public void handlePotClick(PotActor potActor) {
        if (potActor.getPot().isLocked) {
            return;
        } else if (potActor.getPot().getPlantType() == null) {
            controller.plantSeed(potActor.getPot().x, potActor.getPot().y);
            if (potActor.getPot().getPlantType() != null) {
                potActor.setPlant();
            }
        } else if (!potActor.getPot().isReady()) {
            return;
        } else if (potActor.getPot().isReady()) {
            String message = controller.collectPlant(potActor.getPot().x, potActor.getPot().y);
            updateCurrency();
            showMessage(message);
            potActor.updateState();
        }
    }

    public void showMessage(String message) {
        messageNotif.clearActions();

        messageNotif.setText(message);
        messageNotif.setVisible(true);
        messageNotif.pack();
        messageNotif.setPosition((VIRTUAL_WIDTH - messageNotif.getWidth()) * 0.5f, 35f);
        messageNotif.getColor().a = 1f;
        messageNotif.toFront();

        messageNotif.addAction(
            Actions.sequence(
                Actions.delay(2f),
                Actions.fadeOut(0.5f),
                Actions.hide()
            )
        );
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
