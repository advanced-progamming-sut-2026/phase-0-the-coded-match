package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.controllers.PlantController;
import PvZ2.APproject.controllers.PlantSelectionController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.enums.ScreenRelated.GameState;
import PvZ2.APproject.models.*;
import PvZ2.APproject.models.GameMapRelated.Lawnmower;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;
import PvZ2.APproject.models.seasons.EnvironmentEvent;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.views.GameMapView;
import PvZ2.APproject.views.actors.EnvironmentView;
import PvZ2.APproject.views.actors.LawnmowerActor;
import PvZ2.APproject.views.ZombieView;
import PvZ2.APproject.views.actors.PlantBox;
import PvZ2.APproject.views.actors.SunActor;
import PvZ2.APproject.views.menus.ChoosePlantsMenu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
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
    private EnvironmentView environmentView;

    public static float TILE_WIDTH = 80;
    public static float TILE_HEIGHT = 100;
    public static float BOARD_X = 260;
    public static float BOARD_Y = 80;

    private Label messageNotif;
    private Image shovelCursor;
    private float stateTime = 0f;
    private Boolean harvestMood = false;

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

        plantSelectionController = new PlantSelectionController(currentLevel);
        gameMapView = new GameMapView(game, this, currentLevel, textures, backgroundImage, stage);
        environmentView = new EnvironmentView(game, currentLevel, textures);

        backgroundImage = new Image(new TextureRegionDrawable(gameMapView.getBackground()));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        stage.addActor(gameMapView);
        stage.addActor(environmentView);

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


        shovelCursor = new Image(
            new TextureRegionDrawable(
            textures.region(
                "IMAGE_UI_HUD_INGAME_SHOVEL_ICON")
            )
        );

        shovelCursor.setSize(130, 70);
        shovelCursor.setVisible(false);
        stage.addActor(shovelCursor);

        ImageButton harvestBtn = new ImageButton(skin, "ingame_shovel");

        harvestBtn.setPosition(VIRTUAL_WIDTH - harvestBtn.getWidth(), harvestBtn.getHeight() - 60);
        stage.addActor(harvestBtn);

        harvestBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                harvestMood = !harvestMood;
                shovelCursor.setVisible(harvestMood);
            }
        });

    }

    @Override
    public void render(float delta) {
        if (state == GameState.RUNNING) {
            int speedMultiplier = GameSettings.getInstance().getGameSpeed();
            float adjustedSpeed = delta * speedMultiplier;
            super.render(adjustedSpeed);

            stateTime += adjustedSpeed;

            if (harvestMood) {
                updateShovelCursor();
            }

            String message = GameManagerController.getInstance().updateObjects(adjustedSpeed);
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
                environmentView.toFront();
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

    public void updateShovelCursor() {
        Vector2 mousePosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());

        viewport.unproject(mousePosition);

        shovelCursor.setPosition(mousePosition.x - shovelCursor.getWidth() / 2f,
            mousePosition.y - shovelCursor.getHeight() / 2f);
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

        game.setScreen(new ChoosePlantsMenu(game));
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

    public Boolean getHarvestMood() {
        return harvestMood;
    }

    public PlantSelectionController getPlantSelectionController(){
        return plantSelectionController;
    }
}
