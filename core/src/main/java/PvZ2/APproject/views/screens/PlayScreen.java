package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.controllers.PlantController;
import PvZ2.APproject.controllers.PlantSelectionController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.enums.ScreenRelated.GameState;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.GameMapRelated.Lawnmower;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.LevelData;
import PvZ2.APproject.models.Sun;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.views.GameMapView;
import PvZ2.APproject.views.actors.LawnmowerActor;
import PvZ2.APproject.views.ZombieView;
import PvZ2.APproject.views.actors.PlantBox;
import PvZ2.APproject.views.actors.SunActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import pvz.skin.BorderedTable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PlayScreen extends BaseScreen {
    private final Main game;
    private GameState state = GameState.RUNNING;
    private Stage pauseStage;
    private Level currentLevel = GameManagerController.getInstance().getCurrentLevel();
    private GameMapView gameMapView;
    private PlantSelectionController plantSelectionController;

    public static float TILE_WIDTH = 80;
    public static float TILE_HEIGHT = 100;
    public static float BOARD_X = 250;
    public static float BOARD_Y = 80;

    private Label messageNotif;
    private float stateTime = 0f;

    private Map<Sun, SunActor> renderedSuns = new HashMap<>();
    private Map<Lawnmower, LawnmowerActor> renderedLawnmowers = new HashMap<>();
    private Map<Zombie, ZombieView> renderedZombies = new HashMap<>();

    public PlayScreen(Main game) {
        this.game = game;
        pauseStage = new Stage(viewport);
    }

    @Override
    public void show() {
        super.show();

        gameMapView = new GameMapView(game, currentLevel, textures);
        plantSelectionController = new PlantSelectionController(currentLevel);

        backgroundImage = new Image(new TextureRegionDrawable(gameMapView.getBackground()));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        createPlantBoxes();

        messageNotif = new Label("", skin, "promo_ribbon");
        messageNotif.setVisible(false);
        messageNotif.setPosition(265, 50);
        stage.addActor(messageNotif);

        Table mainPauseTable = new Table(skin);
        mainPauseTable.setFillParent(true);
        BorderedTable pauseTable = new BorderedTable();

        Label pauseLabel = new Label("GAME PAUSED", skin, "big");
        TextButton resumeBtn = new TextButton("RESUME", skin);
        TextButton restartBtn = new TextButton("RESTART", skin);
        TextButton exitBtn = new TextButton("EXIT", skin);

        pauseTable.add(pauseLabel).row();
        pauseTable.add(resumeBtn).row();
        pauseTable.add(restartBtn).row();
        pauseTable.add(exitBtn).row();
        pauseTable.center();
        pauseTable.pack();
        mainPauseTable.add(pauseTable);

        pauseStage.addActor(mainPauseTable);

        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                exitGame();
            }
        });

        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                restartGame();
            }
        });

        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resumeGame();
            }
        });
    }

    @Override
    public void render(float delta) {
        if (state == GameState.RUNNING) {
            super.render(delta);

            stateTime += delta;

            String message = GameManagerController.getInstance().updateObjects(delta);
            if (!message.equals("")) {
                showMessage(message);
            }

            updateZombieActors();
            updateSunActors();
            updateLawnmowerActors();
        }

        createPauseButton();

        if (state == GameState.PAUSED) {
            pauseStage.act(delta);
            pauseStage.draw();
        }
    }

    public void createPauseButton() {
        ImageButton pauseButton = new ImageButton(skin, "ingame_pause");
        pauseButton.setPosition(VIRTUAL_WIDTH - pauseButton.getWidth() - 20, VIRTUAL_HEIGHT - pauseButton.getHeight() - 20);
        stage.addActor(pauseButton);

        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseGame();
            }
        });
    }

    public void createPlantBoxes(){
        float x = 20;
        float y = 500;
        for(String plantName : currentLevel.getChosenPlants()){
            PlantData plantData = PlantRepository.getInstance().findByName(plantName);
            if(plantData == null){
                continue;
            }
            PlantBox plantBox = new PlantBox(plantData, plantSelectionController, textures);
            plantBox.setPosition(x, y);
            plantBox.setSize(100, 120);

            stage.addActor(plantBox);

            y -= 130;
        }
    }

    public void updateZombieActors(){
        for(Zombie zombie : currentLevel.getActiveZombies()){
            if(!renderedZombies.containsKey(zombie)){
                ZombieView zombieView = new ZombieView(zombie, game);

                renderedZombies.put(zombie, zombieView);

                stage.addActor(zombieView);
            }
        }

        Iterator<Map.Entry<Zombie, ZombieView>> iterator =
            renderedZombies.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Zombie, ZombieView> entry = iterator.next();

            if (!currentLevel.getActiveZombies().contains(entry.getKey())) {
                entry.getValue().remove();
                iterator.remove();
            }
        }
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

    public void pauseGame() {
        state = GameState.PAUSED;
        Gdx.input.setInputProcessor(pauseStage);
    }

    public void resumeGame() {
        state = GameState.RUNNING;
        Gdx.input.setInputProcessor(stage);
    }

    public void restartGame() {
        state = GameState.RUNNING;
        Gdx.input.setInputProcessor(stage);
        Level newLevel = new Level(currentLevel.getData());

        GameManagerController.getInstance().setCurrentLevel(newLevel);
        GameManagerController.getInstance().getCurrentLevel().setCurrentSeason(App.getCurrentUser().getLastSeason());

        game.setScreen(new PlayScreen(game));
    }

    public void exitGame() {
        Gdx.input.setInputProcessor(stage);
        App.setCurrentMenu(Menu.GAME_MENU);
        game.setScreen(new GameMenuScreen(game));
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public float getStateTime() {
        return stateTime;
    }
}
