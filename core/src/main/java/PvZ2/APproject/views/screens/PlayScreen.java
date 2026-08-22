package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.GameMapRelated.Lawnmower;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.Sun;
import PvZ2.APproject.views.GameMapView;
import PvZ2.APproject.views.actors.LawnmowerActor;
import PvZ2.APproject.views.ZombieView;
import PvZ2.APproject.views.actors.SunActor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.HashMap;
import java.util.Map;

public class PlayScreen extends BaseScreen {
    private final Main game;
    private Level currentLevel = GameManagerController.getInstance().getCurrentLevel();
    private GameMapView gameMapView;
    private ZombieView zombieView;

    private Label messageNotif;
    public float TILE_WIDTH = 80;
    public float TILE_HEIGHT = 100;
    public float BOARD_X = 250;
    public float BOARD_Y = 80;
    private float stateTime = 0f;

    private Map<Sun, SunActor> renderedSuns = new HashMap<>();
    private Map<Lawnmower, LawnmowerActor> renderedLawnmowers = new HashMap<>();

    public PlayScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        super.show();

        gameMapView = new GameMapView(game, currentLevel, textures);
//        gameMapView = new GameMapView(game, textures);

        backgroundImage = new Image(new TextureRegionDrawable(gameMapView.getBackground()));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        messageNotif = new Label("", skin, "promo_ribbon");
        messageNotif.setVisible(false);
        messageNotif.setPosition(265, 50);
        stage.addActor(messageNotif);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stateTime += delta;

        String message = GameManagerController.getInstance().updateObjects(delta);
        if (!message.equals("")) {
            showMessage(message);
        }

        updateSunActors();
        updateLawnmowerActors();
    }

    public void showMessage(String message) {
        messageNotif.clearActions();

        messageNotif.setText(message);
        messageNotif.setVisible(true);
        messageNotif.pack();
        messageNotif.getColor().a = 1f;

        messageNotif.addAction(
            Actions.sequence(
                Actions.delay(2f),
                Actions.fadeOut(0.5f),
                Actions.hide()
            )
        );
    }

    public void updateSunActors() {
        for (Sun sun : currentLevel.getActiveSuns()) {
            if (!renderedSuns.containsKey(sun)) {
                SunActor sunActor = new SunActor(sun, this, player);

                renderedSuns.put(sun, sunActor);

                stage.addActor(sunActor);
            }
        }
    }

    public void updateLawnmowerActors() {
        for (Lawnmower lawnmower : currentLevel.getGameMap().getLawnmowers()) {

            if (lawnmower.HasBeenUsed()) {
                removeLawnmowerActor(lawnmower);
                continue;
            }

            if (!renderedLawnmowers.containsKey(lawnmower)) {
                LawnmowerActor lawnmowerActor = new LawnmowerActor(lawnmower, this, player);

                renderedLawnmowers.put(lawnmower, lawnmowerActor);

                stage.addActor(lawnmowerActor);
            }
        }
    }

    public void removeSunActor(Sun sun) {
        SunActor sunActor = renderedSuns.remove(sun);

        if (sunActor != null) {
            sunActor.remove();
        }
    }

    public void removeLawnmowerActor(Lawnmower lawnmower) {
        LawnmowerActor lawnmowerActor = renderedLawnmowers.remove(lawnmower);

        if (lawnmowerActor != null) {
            lawnmowerActor.remove();
        }
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public float getStateTime() {
        return stateTime;
    }
}
