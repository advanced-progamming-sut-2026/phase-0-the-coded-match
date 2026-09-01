package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.controllers.PlantSelectionController;
import PvZ2.APproject.controllers.menus.GameMenuController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.enums.ScreenRelated.GameState;
import PvZ2.APproject.enums.SpecialLevelType;
import PvZ2.APproject.models.*;
import PvZ2.APproject.models.GameMapRelated.Lawnmower;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.views.GameMapView;
import PvZ2.APproject.views.PlantView;
import PvZ2.APproject.views.ProjectileView;
import PvZ2.APproject.views.actors.EnvironmentView;
import PvZ2.APproject.views.actors.LawnmowerActor;
import PvZ2.APproject.views.ZombieView;
import PvZ2.APproject.views.actors.PlantBox;
import PvZ2.APproject.views.actors.SunActor;
import PvZ2.APproject.views.actors.SunHudActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import pvz.skin.BorderedTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
    private Label missionNotif;
    private Label sunAmountLabel;
    private Label waveLabel;
    private Image shovelCursor;
    private Table resultOverlay;
    private Image resultShade;
    private Texture resultShadeTexture;
    private boolean resultShown;
    private float stateTime = 0f;
    private float simulationAccumulator = 0f;
    private float finishDelay = 0f;
    private static final float FIXED_STEP = 0.1f;
    private Boolean harvestMode = false;
    private Label sunLabel;
    private Label plantFoodLabel;
    private ProgressBar waveProgressBar;

    private Map<Sun, SunActor> renderedSuns = new HashMap<>();
    private Map<Lawnmower, LawnmowerActor> renderedLawnmowers = new HashMap<>();
    private Map<Zombie, ZombieView> renderedZombies = new HashMap<>();
    private Map<Plant, PlantView> renderedPlants = new HashMap<>();
    private Map<Projectile, ProjectileView> renderedProjectiles = new HashMap<>();

    public PlayScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        super.show();
        pauseStage = new Stage(viewport);

        if (currentLevel.getSpecialLevelStrategy() != null) {
            currentLevel.getSpecialLevelStrategy().levelStart(currentLevel);
        }

        plantSelectionController = new PlantSelectionController(currentLevel);
        gameMapView = new GameMapView(game, this, currentLevel, textures, backgroundImage, stage, skin);
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

        missionNotif = new Label("", skin, "bundle_reward_multiplier");
        missionNotif.setVisible(false);
        missionNotif.setPosition(265, VIRTUAL_HEIGHT - missionNotif.getHeight() - 50);
        stage.addActor(missionNotif);

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
        shovelCursor.setTouchable(Touchable.disabled);
        stage.addActor(shovelCursor);

        ImageButton harvestBtn = new ImageButton(skin, "ingame_shovel");

        harvestBtn.setPosition(VIRTUAL_WIDTH - harvestBtn.getWidth(), harvestBtn.getHeight() - 60);
        stage.addActor(harvestBtn);

        harvestBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                harvestMode = !harvestMode;
                if (harvestMode) plantSelectionController.cancelSelection();
                shovelCursor.setVisible(harvestMode);
            }
        });

        addCurrencyBar();

        addSunAndPlantFoodTables();

        Group waveProgressGroup = new Group();

        waveProgressBar = new ProgressBar(0, currentLevel.getZombieWave().getWavePattern().size(), 0.01f,
            false, skin, "ingame_progress");

        waveProgressBar.setSize(300, 40);
        waveProgressGroup.setSize(300, 70);
        waveProgressGroup.setPosition(VIRTUAL_WIDTH - waveProgressBar.getWidth() - 100,
            waveProgressBar.getHeight() - 20);
        waveProgressGroup.addActor(waveProgressBar);

        int waveCount = currentLevel.getZombieWave().getWavePattern().size();

        for (int i = 0; i < waveCount; i++) {

            if (i == waveCount - 1) {
                continue;
            }

            Image flag = new Image(
                new TextureRegionDrawable(
                    textures.region("IMAGE_ZOMBIE_ZOMBIE_BIGHEAD_FLAG_ZOMBIE_BIGHEAD_FLAG_123X95")
                )
            );

            float x;

            if (waveCount == 1) {
                x = waveProgressBar.getWidth() / 2f;
            } else {
                x = i * waveProgressBar.getWidth() / (waveCount - 1f);
            }

            x -= flag.getWidth() / 2f;

            flag.setPosition(x + 40, waveProgressBar.getHeight() - 30);

            flag.setSize(25, 35);

            waveProgressGroup.addActor(flag);
        }

        stage.addActor(waveProgressGroup);

        switch (currentLevel.getData().getSpecialLevelType()) {
            case SpecialLevelType.SAVE_OUR_SEEDS -> {
                showMission("Mission: Save endangered plants");
            }

            case SpecialLevelType.DEAD_LINE -> {
                showMission("Mission: Don't let zombies pass the deadline");
            }

            case SpecialLevelType.LOVE_YOUR_PLANTS -> {
                showMission("Mission: Don't let zombies eat specific plants count");
            }

            default -> {
                showMission("Mission: Defeat all zombies");
            }
        }

        TextButton releaseTheNuke = new TextButton("Release the Nuke", skin, "default");
        releaseTheNuke.setPosition(VIRTUAL_WIDTH - releaseTheNuke.getWidth() - 100, VIRTUAL_HEIGHT - releaseTheNuke.getHeight() - 60);
        releaseTheNuke.setVisible(false);

        if (gameSettings.isDebugMode()) {
            releaseTheNuke.setVisible(true);
        }
        stage.addActor(releaseTheNuke);

        releaseTheNuke.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameManagerController.getInstance().cheatReleaseTheNuke();
            }
        });

        createPauseButton();
    }

    public void addSunAndPlantFoodTables() {
        Table sunAmountTable = new Table(skin);

        Image sunImage = new Image(
            new TextureRegionDrawable(
                textures.region("IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN")
            )
        );
        sunLabel = new Label(Integer.toString(currentLevel.getCollectedSunsAmount()), skin, "default");

        sunAmountTable.add(sunImage).size(36, 36);
        sunAmountTable.add(sunLabel);
        sunAmountTable.pack();
        sunAmountTable.setPosition(
            sunAmountTable.getWidth() + 20, VIRTUAL_HEIGHT - sunAmountTable.getHeight() - 10);


        Table plantFoodTable = new Table(skin);

        Image plantFoodImage = new Image(
            new TextureRegionDrawable(
                textures.region("IMAGE_BACKGROUNDS_TILE_PLANTFOOD_TILE_PLANTFOOD_45X46")
            )
        );
        plantFoodLabel = new Label(currentLevel.getPlantFoodCount() + "/4", skin, "default");

        plantFoodTable.add(plantFoodImage).size(36, 36);
        plantFoodTable.add(plantFoodLabel);
        plantFoodTable.pack();
        plantFoodTable.setPosition(
            plantFoodTable.getWidth() + 20, VIRTUAL_HEIGHT - plantFoodTable.getHeight() - 50
        );


        if (gameSettings.isDebugMode()) {
            TextButton cheatAddSun = new TextButton("+", skin, "default");
            TextField sunAmount = new TextField("", skin, "default");

            TextButton cheatAddPlantFood = new TextButton("+", skin, "default");
            TextField plantFoodAmount = new TextField("", skin, "default");


            sunAmountTable.add(cheatAddSun).size(25, 25);
            sunAmountTable.add(sunAmount).size(50, 25).padLeft(4);
            plantFoodTable.add(cheatAddPlantFood).size(25, 25);
            plantFoodTable.add(plantFoodAmount).size(50, 25).padLeft(4);

            sunAmount.setVisible(false);
            plantFoodAmount.setVisible(false);

            cheatAddSun.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    cheatAddSun.setVisible(false);
                    sunAmount.setVisible(true);
                    stage.setKeyboardFocus(sunAmount);
                }
            });

            sunAmount.addListener(new InputListener() {
                @Override
                public boolean keyDown(InputEvent event, int keycode) {
                    if (keycode == Input.Keys.ENTER) {
                        try {
                            int amount = Integer.parseInt(sunAmount.getText());

                            GameManagerController.getInstance().cheatAddSuns(amount);

                            sunLabel.setText(Integer.toString(currentLevel.getCollectedSunsAmount()));

                            sunAmount.setText("");
                            sunAmount.setVisible(false);
                            cheatAddSun.setVisible(true);

                            stage.setKeyboardFocus(null);

                        } catch (NumberFormatException e) {
                            sunAmount.setText("");
                        }
                        return true;
                    }
                    return false;
                }
            });

            cheatAddPlantFood.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    cheatAddPlantFood.setVisible(false);
                    plantFoodAmount.setVisible(true);
                    stage.setKeyboardFocus(plantFoodAmount);
                }
            });

            plantFoodAmount.addListener(new InputListener() {
                @Override
                public boolean keyDown(InputEvent event, int keycode) {
                    if (keycode == Input.Keys.ENTER) {
                        try {
                            int amount = Integer.parseInt(plantFoodAmount.getText());

                            plantSelectionController.getPlantController().cheatAddPlantFood(amount);

                            plantFoodLabel.setText(currentLevel.getPlantFoodCount() + "/4");

                            plantFoodAmount.setText("");
                            plantFoodAmount.setVisible(false);
                            cheatAddPlantFood.setVisible(true);

                            stage.setKeyboardFocus(null);

                        } catch (NumberFormatException e) {
                            plantFoodAmount.setText("");
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

        stage.addActor(sunAmountTable);
        stage.addActor(plantFoodTable);
    }

    @Override
    public void render(float delta) {
        if (state == GameState.ENDED) {
            if (pauseStage != null) {
                pauseStage.act(delta);
                pauseStage.draw();
            }
            return;
        }

        if (state == GameState.RUNNING) {
            int speedMultiplier = GameSettings.getInstance().getGameSpeed();
            float adjustedSpeed = delta * speedMultiplier;
            super.render(adjustedSpeed);
            stateTime += adjustedSpeed;

            if (harvestMode) {
                updateShovelCursor();
                gameMapView.updateTile();
            }

            simulationAccumulator += adjustedSpeed;
            String message = "";
            int steps = 0;
            while (simulationAccumulator >= FIXED_STEP && steps < 20) {
                String tickMessage = GameManagerController.getInstance().updateObjects(FIXED_STEP);
                if (tickMessage != null && !tickMessage.isEmpty()) message = tickMessage;
                simulationAccumulator -= FIXED_STEP;
                steps++;
                if (GameManagerController.getInstance().isGameFinished()) break;
            }
            if (!message.isEmpty()) {
                showMessage(message);
            }

            updatePlantActors();
            updateZombieActors();
            updateProjectileActors();
            updateSunActors();
            updateLawnmowerActors();
            updateWaveProgressBar();
            if (sunLabel != null) sunLabel.setText(Integer.toString(currentLevel.getCollectedSunsAmount()));
            if (plantFoodLabel != null) plantFoodLabel.setText(currentLevel.getPlantFoodCount() + "/4");

            if (GameManagerController.getInstance().isGameFinished()) {
                finishDelay += adjustedSpeed;
                if (!resultShown && finishDelay >= 0.65f && !hasRunningLawnmower()) {
                    showResultOverlay();
                }
            } else {
                finishDelay = 0f;
            }
        }

        if (state == GameState.PAUSED && pauseStage != null) {
            pauseStage.act(delta);
            pauseStage.draw();
        }
    }

    public void createPauseButton() {
        ImageButton pauseButton = new ImageButton(skin, "ingame_pause");
        pauseButton.setPosition(
            VIRTUAL_WIDTH - pauseButton.getWidth() - 20, VIRTUAL_HEIGHT - pauseButton.getHeight() - 50);
        stage.addActor(pauseButton);

        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseGame();
            }
        });
    }

    public void createPlantBoxes() {
        float x = 16f;
        float y = VIRTUAL_HEIGHT - 175f;
        float width = 92f;
        float height = 100f;
        float gap = 8f;
        for (String plantName : currentLevel.getChosenPlants()) {
            PlantData plantData = PlantRepository.getInstance().findByName(plantName);
            if(plantData == null){
                continue;
            }
            PlantBox plantBox = new PlantBox(plantData, plantSelectionController, textures, skin, stage);
            plantBox.setPosition(x, y);
            plantBox.setSize(width, height);
            stage.addActor(plantBox);
            y -= height + gap;
        }
    }

    public void updatePlantActors() {
        for (Plant plant : currentLevel.getActivePlants()) {
            if (!renderedPlants.containsKey(plant)) {
                PlantView plantView = new PlantView(plant, game);
                renderedPlants.put(plant, plantView);
                stage.addActor(plantView);
            }
        }

        Iterator<Map.Entry<Plant, PlantView>> iterator = renderedPlants.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Plant, PlantView> entry = iterator.next();
            if (!currentLevel.getActivePlants().contains(entry.getKey())) {
                entry.getValue().remove();
                iterator.remove();
            }
        }
    }

    public void updateProjectileActors() {
        for (Projectile projectile : currentLevel.getActiveProjectiles()) {
            if (!renderedProjectiles.containsKey(projectile)) {
                ProjectileView projectileView = new ProjectileView(projectile, this, player);
                renderedProjectiles.put(projectile, projectileView);
                stage.addActor(projectileView);
            }
        }

        Iterator<Map.Entry<Projectile, ProjectileView>> iterator = renderedProjectiles.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Projectile, ProjectileView> entry = iterator.next();
            if (!currentLevel.getActiveProjectiles().contains(entry.getKey())) {
                entry.getValue().remove();
                iterator.remove();
            }
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
                entry.getValue().beginRemoval();
                if (entry.getValue().isRemovalComplete()) {
                    iterator.remove();
                }
            }
        }
    }

    public void showMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }

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

    public void showMission(String mission) {
        missionNotif.clearActions();

        missionNotif.setText(mission);
        missionNotif.setVisible(true);
        missionNotif.pack();
        missionNotif.getColor().a = 1f;

        missionNotif.addAction(
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

        Iterator<Map.Entry<Sun, SunActor>> iterator = renderedSuns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Sun, SunActor> entry = iterator.next();
            if (!currentLevel.getActiveSuns().contains(entry.getKey())) {
                entry.getValue().remove();
                iterator.remove();
            }
        }
    }


    public void updateWaveLabel() {
        if (waveLabel == null || currentLevel == null || currentLevel.getZombieWave() == null) return;
        int total = currentLevel.getZombieWave().getTotalWaves();
        if (total <= 0) {
            waveLabel.setVisible(false);
            return;
        }
        waveLabel.setVisible(true);
        int current = Math.min(total, Math.max(1, currentLevel.getZombieWave().getCurrentWave() + 1));
        waveLabel.setText("WAVE  " + current + " / " + total);
        waveLabel.pack();
        waveLabel.setPosition((VIRTUAL_WIDTH - waveLabel.getWidth()) * 0.5f, VIRTUAL_HEIGHT - 45f);
    }

    private void showResultOverlay() {
        if (resultShown || stage == null) return;
        resultShown = true;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0.68f);
        pixmap.fill();
        resultShadeTexture = new Texture(pixmap);
        pixmap.dispose();

        resultShade = new Image(resultShadeTexture);
        resultShade.setFillParent(true);
        resultShade.setTouchable(Touchable.disabled);
        stage.addActor(resultShade);

        resultOverlay = new Table();
        resultOverlay.setFillParent(true);
        resultOverlay.setTouchable(Touchable.enabled);
        resultOverlay.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        boolean won = App.getCurrentUser() != null && App.getCurrentUser().isVictroy();
        BorderedTable panel = new BorderedTable();
        Label title = new Label(won ? "LEVEL COMPLETE" : "GAME OVER", skin, "big");
        Label text = new Label(won ? "Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz." :
            "The zombie ate your brain; LOSER!!!", skin, "medium");
        TextButton primary = new TextButton("RETRY", skin);
        TextButton exit = new TextButton("EXIT", skin);

        panel.add(title).pad(10f).row();
        panel.add(text).pad(8f).row();
        if (!won) panel.add(primary).width(180f).pad(6f).row();
        panel.add(exit).width(180f).pad(6f).row();
        resultOverlay.add(panel);
        stage.addActor(resultOverlay);

        primary.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (won) exitGame();
                else restartGame();
            }
        });
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                exitGame();
            }
        });
    }

    private void disposeResultShade() {
        if (resultShadeTexture != null) {
            resultShadeTexture.dispose();
            resultShadeTexture = null;
        }
    }

    public void updateSunAmountLabel() {
        if (sunAmountLabel != null) {
            sunAmountLabel.setText(Integer.toString(currentLevel.getCollectedSunsAmount()));
        }
    }

    private boolean hasRunningLawnmower() {
        for (Lawnmower lawnmower : currentLevel.getGameMap().getLawnmowers()) {
            if (lawnmower.isTriggered() && !lawnmower.HasBeenUsed()) {
                return true;
            }
        }
        return false;
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

    private void updateWaveProgressBar() {
        if (waveProgressBar == null || currentLevel == null || currentLevel.getZombieWave() == null) return;
        int totalWaves = currentLevel.getZombieWave().getWavePattern().size();

        if (totalWaves <= 0) {
            return;
        }

        float progress = (currentLevel.getZombieWave().getCurrentWave() + 1f) / totalWaves;

        progress = Math.min(progress, 1f);

        waveProgressBar.setValue(totalWaves - progress * totalWaves);
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
        List<String> chosenPlants = new ArrayList<>(currentLevel.getChosenPlants());
        Level newLevel = new Level(currentLevel.getData());
        newLevel.setCurrentSeason(currentLevel.getCurrentSeason());
        for (String plant : chosenPlants) newLevel.addChosenPlant(plant);
        GameManagerController.getInstance().setCurrentLevel(newLevel);
        if (newLevel.getCurrentSeason() != null) newLevel.getCurrentSeason().LevelStarted(newLevel);
        App.setCurrentMenu(Menu.GAME_MANAGER);
        state = GameState.RUNNING;
        game.setScreen(new PlayScreen(game));
    }

    public void exitGame() {
        Gdx.input.setInputProcessor(stage);
        App.setCurrentMenu(Menu.GAME_MENU);
        game.setScreen(new GameMenuScreen(game));
    }

    public void gameEnded() {
        state = GameState.ENDED;
        if (App.getCurrentUser().isVictroy()) {
            showGameWon();
        } else {
            showGameOver();
        }
        Gdx.input.setInputProcessor(pauseStage);
        App.getCurrentUser().setVictroy(false);
        GameManagerController.getInstance().setIsGameEnded(false);
    }

    public void showGameWon() {
        pauseStage.clear();

        Table mainPauseTable = new Table(skin);
        mainPauseTable.setFillParent(true);
        BorderedTable pauseTable = new BorderedTable();

        Label pauseLabel = new Label(
            "Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.",
            skin, "big");
        TextButton exitBtn = new TextButton("EXIT", skin, "purple");

        pauseTable.add(pauseLabel).row();
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
    }

    public void showGameOver() {
        pauseStage.clear();

        Table mainPauseTable = new Table(skin);
        mainPauseTable.setFillParent(true);
        BorderedTable pauseTable = new BorderedTable();

        Label pauseLabel = new Label("The zombie ate your brain; LOSER!!!", skin, "big");
        TextButton restartBtn = new TextButton("RESTART", skin, "brown");
        TextButton exitBtn = new TextButton("EXIT", skin, "brown");

        pauseTable.add(pauseLabel).row();
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
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public float getStateTime() {
        return stateTime;
    }

    public Boolean getHarvestMode() {
        return harvestMode;
    }

    public PlantSelectionController getPlantSelectionController(){
        return plantSelectionController;
    }

    @Override
    public void hide() {
        if (gameMapView != null) gameMapView.dispose();
        super.hide();
        if (pauseStage != null) {
            pauseStage.dispose();
            pauseStage = null;
        }
        disposeResultShade();
    }

    @Override
    public void dispose() {
        if (gameMapView != null) gameMapView.dispose();
        super.dispose();
        if (pauseStage != null) {
            pauseStage.dispose();
            pauseStage = null;
        }
        disposeResultShade();
    }
}
