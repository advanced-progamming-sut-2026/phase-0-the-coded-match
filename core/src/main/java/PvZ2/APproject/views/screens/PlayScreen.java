package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.audio.MusicManager;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.controllers.MiniGameController;
import PvZ2.APproject.controllers.PlantSelectionController;
import PvZ2.APproject.controllers.ReactionController;
import PvZ2.APproject.enums.BowlingNutType;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.enums.ScreenRelated.GameState;
import PvZ2.APproject.models.*;
import PvZ2.APproject.models.GameMapRelated.Lawnmower;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.MiniGameRelated.Beghouled;
import PvZ2.APproject.models.MiniGameRelated.IZombie;
import PvZ2.APproject.models.MiniGameRelated.MiniGame;
import PvZ2.APproject.models.MiniGameRelated.VaseBreaker;
import PvZ2.APproject.models.MiniGameRelated.WallNutBowling;
import PvZ2.APproject.models.MiniGameRelated.Zombotany;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.views.GameMapView;
import PvZ2.APproject.views.PlantView;
import PvZ2.APproject.views.ProjectileView;
import PvZ2.APproject.views.actors.EnvironmentView;
import PvZ2.APproject.views.actors.LawnmowerActor;
import PvZ2.APproject.views.actors.MiniGamePamActor;
import PvZ2.APproject.views.ZombieView;
import PvZ2.APproject.views.actors.PlantBox;
import PvZ2.APproject.views.actors.SunActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import PvZ2.APproject.models.zombies.ZombieData;
import com.badlogic.gdx.utils.Scaling;
import pvz.skin.BorderedTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PlayScreen extends BaseScreen {
    private final Main game;
    private GameState state = GameState.RUNNING;
    private Stage pauseStage;
    private Level currentLevel = GameManagerController.getInstance().getCurrentLevel();
    private GameMapView gameMapView;
    private PlantSelectionController plantSelectionController;
    private EnvironmentView environmentView;

    public static float TILE_WIDTH = 100f;
    public static float TILE_HEIGHT = 93.75f;
    public static float BOARD_X = 325f;
    public static float BOARD_Y = 75f;

    private Label messageNotif;
    private Label missionNotif;
    private Table multiplayerChatPanel;
    private Label multiplayerIncomingNotif;
    private static final String[] QUICK_MESSAGES = {
        "Good luck!",
        "Nice move!",
        "Well played!"
    };

    private static final String[] QUICK_EMOJIS = {
        "bruh",
        "mashti",
        "yummy"
    };
    private final Map<String, TextureRegion> emojiTextures = new HashMap<>();
    private Image multiplayerIncomingEmoji;
    private final Consumer<Response> reactionListener = this::handleIncomingReaction;
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
    private final Map<VaseBreaker.Vase, MiniGamePamActor> renderedVases = new HashMap<>();
    private final Map<RollingNut, PamActor> renderedRollingNuts = new HashMap<>();
    private final List<MiniGamePamActor> brainActors = new ArrayList<>();
    private final Map<Zombotany.ZombiePea, MiniGamePamActor> renderedZombiePeas = new HashMap<>();
    private final Map<Zombie, PamActor> renderedPlantZombieHeads = new HashMap<>();
    private final Map<String, Label> miniZombieCooldownLabels = new HashMap<>();
    private Table miniGamePanel;
    private Label miniGameStatusLabel;
    private String selectedMiniZombie;
    private String selectedMiniPlant;
    private Tile selectedBeghouledTile;
    private String vaseSeedSignature = "";
    private String bowlingSignature = "";
    private final List<BowlingNutType> bowlingUiSnapshot = new ArrayList<>();

    private float networkPollTimer = 0f;
    private static final float NETWORK_POLL_INTERVAL = 0.1f;
    private boolean finalWaveMusic;

    public PlayScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        super.show();
        MusicManager.playForLevel(currentLevel);
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
        createMiniGameUi();
        System.out.println("LOADING EMOJIS...");
        emojiTextures.put("mashti", new TextureRegion(new Texture(Gdx.files.internal("emoji/mashti.jpeg"))));
        emojiTextures.put("bruh", new TextureRegion(new Texture(Gdx.files.internal("emoji/bruh.png"))));
        emojiTextures.put("yummy", new TextureRegion(new Texture(Gdx.files.internal("emoji/yummy.jpeg"))));
        System.out.println("Loaded emojis: " + emojiTextures.keySet());
        createMultiplayerCommunicationUi();
        ReactionController.setIncomingListener(
            reactionListener
        );

        messageNotif = new Label("", skin, "promo_ribbon");
        messageNotif.setVisible(false);
        messageNotif.setPosition(265, 50);
        stage.addActor(messageNotif);

        missionNotif = new Label("", skin, "bundle_reward_multiplier");
        missionNotif.setVisible(false);
        missionNotif.setPosition(265, VIRTUAL_HEIGHT - missionNotif.getHeight() - 50);
        stage.addActor(missionNotif);

        multiplayerIncomingNotif = new Label("", skin, "promo_ribbon");
        multiplayerIncomingNotif.setVisible(false);
        multiplayerIncomingNotif.setPosition(VIRTUAL_WIDTH / 2f - 150f, VIRTUAL_HEIGHT - 150f);
        stage.addActor(multiplayerIncomingNotif);

        multiplayerIncomingEmoji = new Image();
        multiplayerIncomingEmoji.setVisible(false);
        multiplayerIncomingEmoji.setSize(80f, 80f);
        multiplayerIncomingEmoji.setPosition(
            VIRTUAL_WIDTH / 2f - 40f,
            VIRTUAL_HEIGHT - 230f
        );
        stage.addActor(multiplayerIncomingEmoji);

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
        harvestBtn.setVisible(!(currentLevel instanceof IZombie) &&
            !(currentLevel instanceof WallNutBowling) && !(currentLevel instanceof Beghouled));
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

        if (!(currentLevel instanceof MiniGame)) {
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
                if (i == waveCount - 1) continue;
                Image flag = new Image(new TextureRegionDrawable(
                    textures.region("IMAGE_ZOMBIE_ZOMBIE_BIGHEAD_FLAG_ZOMBIE_BIGHEAD_FLAG_123X95")));
                float x = waveCount == 1 ? waveProgressBar.getWidth() / 2f
                    : i * waveProgressBar.getWidth() / (waveCount - 1f);
                x -= flag.getWidth() / 2f;
                flag.setPosition(x + 40, waveProgressBar.getHeight() - 30);
                flag.setSize(25, 35);
                waveProgressGroup.addActor(flag);
            }
            stage.addActor(waveProgressGroup);
        }

        if (currentLevel instanceof MiniGame) {
            showMission(getMiniGameMission());
        } else if (currentLevel.getData().getSpecialLevelType() != null) {
            switch (currentLevel.getData().getSpecialLevelType()) {
                case SAVE_OUR_SEEDS -> showMission("Mission: Save endangered plants");
                case DEAD_LINE -> showMission("Mission: Don't let zombies pass the deadline");
                case LOVE_YOUR_PLANTS -> showMission("Mission: Don't let zombies eat specific plants count");
                default -> showMission("Mission: Defeat all zombies");
            }
        } else {
            showMission("Mission: Defeat all zombies");
        }

        TextButton releaseTheNuke = new TextButton("Release the Nuke", skin, "default");
        releaseTheNuke.setPosition(VIRTUAL_WIDTH - releaseTheNuke.getWidth() - 100,
            VIRTUAL_HEIGHT - releaseTheNuke.getHeight() - 60);
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
        sunLabel = new Label(Integer.toString(getDisplayedSunAmount()), skin, "default");

        sunAmountTable.add(sunImage).size(36, 36);
        sunAmountTable.add(sunLabel);
        sunAmountTable.pack();
        sunAmountTable.setPosition(
            124f, VIRTUAL_HEIGHT - sunAmountTable.getHeight() - 12f);


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
            124f, VIRTUAL_HEIGHT - plantFoodTable.getHeight() - 56f
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

        sunAmountTable.setVisible(!(currentLevel instanceof VaseBreaker) && !(currentLevel instanceof WallNutBowling));
        plantFoodTable.setVisible(!(currentLevel instanceof MiniGame) || currentLevel instanceof Zombotany);
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
            if (!message.isEmpty()) {showMessage(message);}
            if (!(currentLevel instanceof MiniGame) && currentLevel.getZombieWave().isLastWave() && !finalWaveMusic) {
                MusicManager.playFinalWave();
                finalWaveMusic = true;}
            updatePlantActors();updateZombieActors();updateProjectileActors();
            updateSunActors();updateLawnmowerActors();updateWaveProgressBar();updateMiniGameActors();updateMiniGameUi();
            if (sunLabel != null) sunLabel.setText(Integer.toString(getDisplayedSunAmount()));
            if (plantFoodLabel != null) plantFoodLabel.setText(currentLevel.getPlantFoodCount() + "/4");
            if (GameManagerController.getInstance().isGameFinished()) {
                finishDelay += adjustedSpeed;
                if (!resultShown && finishDelay >= 0.65f && !hasRunningLawnmower()) {showResultOverlay();}
            } else {finishDelay = 0f;}
        }
        if (state == GameState.PAUSED && pauseStage != null) {
            pauseStage.act(delta);pauseStage.draw();
        }
    }

    public void createPauseButton() {
        ImageButton pauseButton = new ImageButton(skin, "ingame_pause");
        pauseButton.setPosition(
            VIRTUAL_WIDTH - pauseButton.getWidth() - 18f, VIRTUAL_HEIGHT - pauseButton.getHeight() - 74f);
        stage.addActor(pauseButton);

        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseGame();
            }
        });
    }

    public void createPlantBoxes() {
        if (currentLevel instanceof VaseBreaker || currentLevel instanceof IZombie ||
            currentLevel instanceof WallNutBowling || currentLevel instanceof Beghouled) return;
        float x = 14f;
        float width = 96f;
        float gap = 3f;
        int count = Math.max(1, currentLevel.getChosenPlants().size());
        float height = Math.min(88f, (VIRTUAL_HEIGHT - 92f - gap * (count - 1)) / count);
        float y = VIRTUAL_HEIGHT - height - 8f;
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


    private void createMiniGameUi() {
        if (!(currentLevel instanceof MiniGame)) return;
        miniGamePanel = new BorderedTable();
        miniGamePanel.top().left();
        miniGameStatusLabel = new Label("", skin, "default");
        if (currentLevel instanceof IZombie gameMode) {
            createIZombieUi(gameMode);
            createBrainActors(gameMode);
        } else if (currentLevel instanceof VaseBreaker) {
            miniGamePanel.setPosition(12f, 150f);
            stage.addActor(miniGamePanel);
            refreshVaseSeedPanel(true);
        } else if (currentLevel instanceof WallNutBowling) {
            stage.addActor(miniGamePanel);
            refreshBowlingPanel(true);
        } else if (currentLevel instanceof Beghouled gameMode) {
            createBeghouledUi(gameMode);
        } else if (currentLevel instanceof Zombotany gameMode) {
            miniGameStatusLabel.setText("ZOMBOTANY  STAGE " + gameMode.getStageNumber());
            miniGameStatusLabel.pack();
            miniGameStatusLabel.setPosition(18f, VIRTUAL_HEIGHT - 115f);
            stage.addActor(miniGameStatusLabel);
        }
    }

    private void createMultiplayerCommunicationUi() {
        if (!MiniGameController.isNetworkedIZombie()) {
            return;
        }
        multiplayerChatPanel = new BorderedTable();
        multiplayerChatPanel.top().center();
        Label title = new Label("REACTIONS", skin, "default");
        title.setFontScale(0.75f);
        multiplayerChatPanel.add(title).padBottom(5f).row();
        for (String message : QUICK_MESSAGES) {
            TextButton button = new TextButton(message, skin);
            button.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        ReactionController.send("TEXT", message);
                    }
                }
            );
            multiplayerChatPanel.add(button).width(140f).height(35f).pad(2f).row();
        }
        Table emojiTable = new Table(skin);

        for (String emoji : QUICK_EMOJIS) {
            TextureRegion region = emojiTextures.get(emoji);
            if (region == null) {
                System.out.println("Missing texture for: " + emoji);
                continue;
            }
            TextureRegionDrawable drawable = new TextureRegionDrawable(region);
            ImageButton button = new ImageButton(drawable);
            button.getImage().setScaling(Scaling.fit);
            button.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        ReactionController.send("EMOJI", emoji);
                    }
                }
            );
            emojiTable.add(button).size(42f).pad(2f);
        }
        multiplayerChatPanel.add(emojiTable).padTop(5f).row();
        multiplayerChatPanel.pack();
        multiplayerChatPanel.setPosition(15f, VIRTUAL_HEIGHT / 2f - multiplayerChatPanel.getHeight() / 2f);
        stage.addActor(multiplayerChatPanel);
    }

    public void handleIncomingReaction(Response response){
        String from = response.get("from");
        String kind = response.get("kind");
        String value = response.get("value");
        if (from == null) {
            from = "Opponent";
        }
        if (value == null) {
            return;
        }
        showIncomingReaction(from, kind, value);
    }

    public void showIncomingReaction(String username, String kind, String content) {
        if (multiplayerIncomingNotif == null) {
            return;
        }
        String text;
        if ("EMOJI".equalsIgnoreCase(kind)) {
            showIncomingEmoji(username, content);
        } else {
            showIncomingText(username, content);
        }
//
//        multiplayerIncomingNotif.clearActions();
//        multiplayerIncomingNotif.setText(username +": "+ co);
//        multiplayerIncomingNotif.pack();
//        multiplayerIncomingNotif.setVisible(true);
//        multiplayerIncomingNotif.getColor().a = 1f;
//        multiplayerIncomingNotif.addAction(Actions.sequence(Actions.delay(3f), Actions.fadeOut(0.5f), Actions.hide())
//        );
    }

    private void showIncomingText(String username, String text){
        multiplayerIncomingNotif.clearActions();
        multiplayerIncomingNotif.setText(username + " :"+ text);
        multiplayerIncomingNotif.pack();
        multiplayerIncomingNotif.setVisible(true);
        multiplayerIncomingNotif.getColor().a = 1f;
        multiplayerIncomingNotif.addAction(Actions.sequence(Actions.delay(3f), Actions.fadeOut(0.5f), Actions.hide()));
    }

    private void showIncomingEmoji(String username, String emojiId){
        TextureRegion region = emojiTextures.get(emojiId);

        if (region == null) {
            return;
        }
        multiplayerIncomingNotif.setText(username + ":");
        multiplayerIncomingNotif.pack();
        multiplayerIncomingNotif.setVisible(true);
        multiplayerIncomingNotif.getColor().a = 1f;

        multiplayerIncomingEmoji.setDrawable(new TextureRegionDrawable(region));
        multiplayerIncomingEmoji.setVisible(true);
        multiplayerIncomingEmoji.getColor().a = 1f;
        multiplayerIncomingEmoji.clearActions();
        multiplayerIncomingEmoji.addAction(Actions.sequence(Actions.delay(3f), Actions.fadeOut(0.5f), Actions.hide()));
        multiplayerIncomingNotif.clearActions();
        multiplayerIncomingNotif.addAction(Actions.sequence(Actions.delay(3f), Actions.fadeOut(0.5f), Actions.hide()));
    }

    private void createIZombieUi(IZombie gameMode) {
        miniGamePanel.clearChildren();
        miniZombieCooldownLabels.clear();

//        Label title = new Label("I, ZOMBIE", skin, "default");
//        title.setFontScale(0.9f);
        Label title;
        if (MiniGameController.isPlantsPlayer()) {
            title = new Label("I, ZOMBIE - PLANTS", skin, "default");
        } else {
            title = new Label("I, ZOMBIE - ZOMBIES", skin, "default");
        }
        title.setFontScale(0.9f);
        miniGamePanel.add(title).padLeft(8f).padRight(8f);
        if (MiniGameController.isPlantsPlayer()) {
            createIZombiePlantSelection(gameMode);
        }
        else {
            createIZombieZombieSelection(gameMode);
        }
        miniGamePanel.pack();
        miniGamePanel.setPosition(BOARD_X, BOARD_Y + TILE_HEIGHT * 5f + 8f);
        stage.addActor(miniGamePanel);
        if (MiniGameController.isPlantsPlayer()) {
            miniGameStatusLabel.setText("Select a plant");
        } else {
            miniGameStatusLabel.setText("Select a zombie");
        }
        miniGameStatusLabel.pack();
        miniGameStatusLabel.setPosition(BOARD_X, BOARD_Y + TILE_HEIGHT * 5f - 18f);
        stage.addActor(miniGameStatusLabel);
    }

    public void createIZombieZombieSelection(IZombie gameMode){
        for (String zombieName : gameMode.getAvailableZombies()) {
            ZombieData data = gameMode.getAvailableZombieData(zombieName);
            BorderedTable packet = new BorderedTable();
            packet.top().center();
            packet.setTouchable(Touchable.enabled);

            PamActor icon = new PamActor(game, PamActor.Kind.ZOMBIE, "idle",
                data == null ? zombieName : data.getId(), zombieName,
                data == null ? zombieName : data.getPath());
            icon.setSize(72f, 68f);

            String displayName = data == null ? zombieName : data.getDisplayName();
            Label name = new Label(displayName, skin, "default");
            name.setFontScale(0.68f);

            Table bottom = new Table(skin);
            Image sun = new Image(new TextureRegionDrawable(
                textures.region("IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN")
            ));
            sun.setSize(15f, 15f);
            Label cost = new Label(data == null ? "?" : Integer.toString(data.getCost()), skin, "default");
            cost.setFontScale(0.72f);
            Label cooldown = new Label("READY", skin, "default");
            cooldown.setFontScale(0.62f);
            miniZombieCooldownLabels.put(zombieName, cooldown);

            bottom.add(sun).size(15f, 15f).padRight(2f);
            bottom.add(cost).padRight(5f);
            bottom.add(cooldown);
            packet.add(icon).size(72f, 68f).row();
            packet.add(name).width(105f).center().row();
            packet.add(bottom).width(105f).center();

            packet.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedMiniZombie = zombieName;
                    selectedMiniPlant = null;
                    if (miniGameStatusLabel != null) {
                        miniGameStatusLabel.setText(
                            "Selected: " + displayName
                        );
                    }
                }
            });
            miniGamePanel.add(packet).width(116f).height(106f).pad(3f);
        }
    }

    private void createIZombiePlantSelection(IZombie gameMode) {
        for (String plantName : gameMode.getAvailablePlants()) {
            PlantData data = PlantRepository.getInstance().findByName(plantName);
            if (data == null) {
                continue;
            }
            BorderedTable packet = new BorderedTable();
            packet.top().center();
            packet.setTouchable(Touchable.enabled);

            PamActor icon = new PamActor(game, PamActor.Kind.PLANT, "idle",
                data.getId(), data.getName(), data.getDisplayName());
            icon.setSize(72f, 68f);
            Label name = new Label(data.getDisplayName(), skin, "default");
            name.setFontScale(0.68f);
            Table bottom = new Table(skin);

            Image sun = new Image(new TextureRegionDrawable(textures.region(
                "IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN")));
            sun.setSize(15f, 15f);
//            Label cost = new Label(Integer.toString(data.getCost()), skin, "default");
//            cost.setFontScale(0.72f);
            bottom.add(sun).size(15f, 15f).padRight(2f);
//            bottom.add(cost);
            packet.add(icon).size(72f, 68f).row();
            packet.add(name).width(105f).center().row();
            packet.add(bottom).width(105f).center();

            packet.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String error = plantSelectionController.selectPlant(data);
                    if (error != null && !error.isEmpty()) {
                        showMessage(error);
                        return;
                    }
                    selectedMiniPlant = plantName;
                    selectedMiniZombie = null;
                    if (miniGameStatusLabel != null) {
                        miniGameStatusLabel.setText("Selected: " + data.getDisplayName());
                        }
                    }
                }
            );
            miniGamePanel.add(packet).width(116f).height(106f).pad(3f);
        }
    }

    private void createBrainActors(IZombie gameMode) {
        for (int row = 1; row <= 5; row++) {
            MiniGamePamActor brain = new MiniGamePamActor(game,
                "768/FULL/EFFECTS/BRAIN_EFFECT/BRAIN_EFFECT.PAM", "idle");
            brain.setBounds(BOARD_X - 58f, BOARD_Y + (row - 1) * TILE_HEIGHT + 12f, 60f, 72f);
            brainActors.add(brain);
            stage.addActor(brain);
        }
    }

    private void createBeghouledUi(Beghouled gameMode) {
        miniGamePanel.clearChildren();

        Label title = new Label("BEGHOULED", skin, "default");
        title.setFontScale(0.9f);
        miniGamePanel.add(title).padLeft(8f).padRight(8f);

        addBeghouledUpgrade("Peashooter", "Repeater");
        addBeghouledUpgrade("Wall-nut", "Tall-nut");
        addBeghouledUpgrade("Puff-shroom", "Fume-shroom");
        addBeghouledUpgrade("Cabbage-pult", "Melon-pult");

        miniGamePanel.pack();
        miniGamePanel.setPosition(BOARD_X, BOARD_Y + TILE_HEIGHT * 5f + 8f);
        stage.addActor(miniGamePanel);

        miniGameStatusLabel.setText("MATCHES " + gameMode.getMatchCount() + " / " + gameMode.getTargetMatches());
        miniGameStatusLabel.pack();
        miniGameStatusLabel.setPosition(BOARD_X, BOARD_Y + TILE_HEIGHT * 5f - 18f);
        stage.addActor(miniGameStatusLabel);
    }

    private void addBeghouledUpgrade(String from, String to) {
        PlantData fromData = PlantRepository.getInstance().findByName(from);
        PlantData toData = PlantRepository.getInstance().findByName(to);

        BorderedTable card = new BorderedTable();
        card.setTouchable(Touchable.enabled);

        PamActor fromIcon = new PamActor(game, PamActor.Kind.PLANT, "idle",
            fromData == null ? from : fromData.getId(), from);
        PamActor toIcon = new PamActor(game, PamActor.Kind.PLANT, "idle",
            toData == null ? to : toData.getId(), to);
        fromIcon.setSize(48f, 54f);
        toIcon.setSize(48f, 54f);

        Label arrow = new Label(">", skin, "default");
        Label names = new Label(from + "\n" + to, skin, "default");
        names.setFontScale(0.60f);

        Table icons = new Table(skin);
        icons.add(fromIcon).size(48f, 54f);
        icons.add(arrow).padLeft(2f).padRight(2f);
        icons.add(toIcon).size(48f, 54f);

        card.add(icons).row();
        card.add(names).width(128f).center();

        card.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentLevel instanceof Beghouled gameMode) showMessage(gameMode.upgradePlants(from, to));
            }
        });

        miniGamePanel.add(card).width(142f).height(94f).pad(3f);
    }

    private void refreshVaseSeedPanel(boolean force) {
        if (!(currentLevel instanceof VaseBreaker gameMode) || miniGamePanel == null) return;
        Map<String, Integer> counts = new HashMap<>();
        for (String seed : gameMode.getCollectedSeedPackets()) counts.merge(seed, 1, Integer::sum);
        String signature = counts.toString();
        if (!force && signature.equals(vaseSeedSignature)) return;
        vaseSeedSignature = signature;
        miniGamePanel.clearChildren();
        miniGamePanel.add(new Label("SEED PACKETS", skin, "default")).padBottom(4f).row();
        if (counts.isEmpty()) {
            miniGamePanel.add(new Label("Break vases to get plants", skin, "default")).row();
        } else {
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                PlantData data = PlantRepository.getInstance().findByName(entry.getKey());
                if (data == null) continue;
                Table row = new Table(skin);
                PamActor icon = new PamActor(game, PamActor.Kind.PLANT, "idle",
                    data.getId(), data.getName(), data.getDisplayName());
                icon.setSize(50f, 58f);
                TextButton button = new TextButton(data.getDisplayName() + " x" + entry.getValue(),
                    skin, "default");
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        harvestMode = false;
                        shovelCursor.setVisible(false);
                        plantSelectionController.selectPlant(data);
                    }
                });
                row.add(icon).size(50f, 58f);
                row.add(button).width(150f).height(40f);
                miniGamePanel.add(row).row();
            }
        }
        miniGamePanel.pack();
    }

    private void refreshBowlingPanel(boolean force) {
        if (!(currentLevel instanceof WallNutBowling gameMode) || miniGamePanel == null) return;
        List<BowlingNutType> belt = new ArrayList<>(gameMode.getConveyorBelt());
        String signature = belt.toString();
        if (!force && signature.equals(bowlingSignature)) return;
        boolean appended = !bowlingUiSnapshot.isEmpty()
            && belt.size() == bowlingUiSnapshot.size() + 1
            && belt.subList(0, bowlingUiSnapshot.size()).equals(bowlingUiSnapshot);
        bowlingSignature = signature;
        bowlingUiSnapshot.clear();
        bowlingUiSnapshot.addAll(belt);
        miniGamePanel.clearChildren();
        Label title = new Label("WALL-NUT BOWLING", skin, "default");
        title.setFontScale(0.85f);
        miniGamePanel.add(title).padLeft(8f).padRight(8f);
        int index = 0;
        for (BowlingNutType type : belt) {
            if (index >= 5) break;
            BorderedTable packet = new BorderedTable();
            String key = type == BowlingNutType.EXPLODE_O_NUT ? "explodeonut" : "wallnut";
            PamActor icon = new PamActor(game, PamActor.Kind.PLANT, "idle", key);
            float iconSize = type == BowlingNutType.GIANT_WALLNUT ? 65f : 56f;
            icon.setSize(iconSize, iconSize);
            Label name = new Label(formatNutName(type), skin, "default");
            name.setFontScale(0.58f);
            packet.add(icon).size(iconSize, iconSize).row();
            packet.add(name).width(92f).center();
            if (force) {
                packet.getColor().a = 0f;
                packet.addAction(Actions.sequence(
                    Actions.delay(index * 0.18f),
                    Actions.parallel(
                        Actions.fadeIn(0.32f),
                        Actions.sequence(Actions.moveBy(70f, 0f), Actions.moveBy(-70f, 0f, 0.42f))
                    )
                ));
            } else if (appended && index == belt.size() - 1) {
                packet.getColor().a = 0f;
                packet.addAction(Actions.parallel(
                    Actions.fadeIn(0.32f),
                    Actions.sequence(Actions.moveBy(70f, 0f), Actions.moveBy(-70f, 0f, 0.42f))
                ));
            }
            miniGamePanel.add(packet).width(102f).height(92f).pad(2f);
            index++;
        }
        miniGamePanel.pack();
        miniGamePanel.setPosition(BOARD_X, BOARD_Y + TILE_HEIGHT * 5f + 8f);
    }

    private String formatNutName(BowlingNutType type) {
        return switch (type) {
            case EXPLODE_O_NUT -> "Explode-o-nut";
            case GIANT_WALLNUT -> "Giant Wall-nut";
            default -> "Wall-nut";
        };
    }

    private void updateMiniGameUi() {
        if (currentLevel instanceof VaseBreaker) {
            refreshVaseSeedPanel(false);
        } else if (currentLevel instanceof WallNutBowling) {
            refreshBowlingPanel(false);
        } else if (currentLevel instanceof IZombie gameMode) {
            for (Map.Entry<String, Label> entry : miniZombieCooldownLabels.entrySet()) {
                int ticks = gameMode.getZombieCooldown(entry.getKey());
                entry.getValue().setText(ticks <= 0 ? "READY" : String.format("%.1fs", ticks / 10f));
            }
            for (int row = 1; row <= brainActors.size(); row++) {
                brainActors.get(row - 1).setVisible(!gameMode.isBrainEaten(row));
            }
        } else if (currentLevel instanceof Beghouled gameMode && miniGameStatusLabel != null) {
            miniGameStatusLabel.setText("MATCHES " + gameMode.getMatchCount() + " / " + gameMode.getTargetMatches());
        }
    }

    private void updateMiniGameActors() {
        updateVaseActors();
        updateRollingNutActors();
        updateZombotanyPeas();
        updatePlantZombieHeads();
    }

    private void updateVaseActors() {
        if (!(currentLevel instanceof VaseBreaker gameMode)) return;
        for (VaseBreaker.Vase vase : gameMode.getActiveVases()) {
            if (vase.isBroken()) continue;
            if (!renderedVases.containsKey(vase)) {
                MiniGamePamActor actor = new MiniGamePamActor(game, getVasePamPath(vase), "idle");
                actor.setBounds(BOARD_X + (vase.getX() - 1) * TILE_WIDTH,
                    BOARD_Y + (vase.getY() - 1) * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                renderedVases.put(vase, actor);
                stage.addActor(actor);
            }
        }
        Iterator<Map.Entry<VaseBreaker.Vase, MiniGamePamActor>> iterator = renderedVases.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<VaseBreaker.Vase, MiniGamePamActor> entry = iterator.next();
            if (entry.getKey().isBroken()) {
                entry.getValue().remove();
                iterator.remove();
            }
        }
    }

    private String getVasePamPath(VaseBreaker.Vase vase) {
        return switch (vase.getVaseType()) {
            case PLANT_VASE -> "768/FULL/VASEBREAKER/VASE_GREEN/VASE_GREEN.PAM";
            case GARGANTUAR_VASE -> "768/FULL/VASEBREAKER/VASE_GARGANTUAR/VASE_GARGANTUAR.PAM";
            default -> "768/FULL/VASEBREAKER/VASE_BROWN/VASE_BROWN.PAM";
        };
    }

    private void updateRollingNutActors() {
        if (!(currentLevel instanceof WallNutBowling gameMode)) return;
        for (RollingNut nut : gameMode.getActiveRollingNuts()) {
            PamActor actor = renderedRollingNuts.get(nut);
            if (actor == null) {
                String key = nut.getNutType() == BowlingNutType.EXPLODE_O_NUT ? "explodeonut" : "wallnut";
                actor = new PamActor(game, PamActor.Kind.PLANT, "idle", key);
                renderedRollingNuts.put(nut, actor);
                stage.addActor(actor);
            }
            float width = nut.getNutType() == BowlingNutType.GIANT_WALLNUT ? 96f : 70f;
            float height = nut.getNutType() == BowlingNutType.GIANT_WALLNUT ? 115f : 88f;
            actor.setSize(width, height);
            actor.setPosition(BOARD_X + (float) (nut.getXCoordinate() - 1) * TILE_WIDTH + (TILE_WIDTH - width) / 2f,
                BOARD_Y + (float) (nut.getYCoordinate() - 1) * TILE_HEIGHT + (TILE_HEIGHT - height) / 2f);
        }
        Iterator<Map.Entry<RollingNut, PamActor>> iterator = renderedRollingNuts.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<RollingNut, PamActor> entry = iterator.next();
            if (!gameMode.getActiveRollingNuts().contains(entry.getKey())) {
                entry.getValue().remove();
                iterator.remove();
            }
        }
    }

    private void updateZombotanyPeas() {
        if (!(currentLevel instanceof Zombotany gameMode)) return;
        for (Zombotany.ZombiePea pea : gameMode.getActivePeas()) {
            if (!renderedZombiePeas.containsKey(pea)) {
                MiniGamePamActor actor = new MiniGamePamActor(game,
                    "768/INITIAL/EFFECTS/T_PEA_PROJECTILE/T_PEA_PROJECTILE.PAM", "idle");
                actor.setSize(35f, 35f);
                renderedZombiePeas.put(pea, actor);
                stage.addActor(actor);
            }
            MiniGamePamActor actor = renderedZombiePeas.get(pea);
            actor.setPosition(BOARD_X + (float) (pea.getX() - 1) * TILE_WIDTH + TILE_WIDTH * 0.3f,
                BOARD_Y + (pea.getRow() - 1) * TILE_HEIGHT + TILE_HEIGHT * 0.42f);
        }
        Iterator<Map.Entry<Zombotany.ZombiePea, MiniGamePamActor>> iterator = renderedZombiePeas.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Zombotany.ZombiePea, MiniGamePamActor> entry = iterator.next();
            if (!gameMode.getActivePeas().contains(entry.getKey())) {
                entry.getValue().remove();
                iterator.remove();
            }
        }
    }

    private void updatePlantZombieHeads() {
        if (!(currentLevel instanceof Zombotany gameMode)) return;
        for (Zombie zombie : currentLevel.getActiveZombies()) {
            Zombotany.PlantZombieType type = gameMode.getTypeOf(zombie);
            if (type == null) continue;
            PamActor actor = renderedPlantZombieHeads.get(zombie);
            if (actor == null) {
                String key = switch (type) {
                    case PEASHOOTER -> "peashooter";
                    case WALL_NUT -> "wallnut";
                    case JALAPENO -> "jalapeno";
                    case SQUASH -> "squash";
                };
                actor = new PamActor(game, PamActor.Kind.PLANT, "idle", key);
                actor.setSize(46f, 54f);
                renderedPlantZombieHeads.put(zombie, actor);
                stage.addActor(actor);
            }
            ZombieView zombieView = renderedZombies.get(zombie);
            if (zombieView != null) {
                actor.setSize(34f, 40f);
                actor.setPosition(
                    zombieView.getX() + zombieView.getWidth() * 0.58f,
                    zombieView.getY() + zombieView.getHeight() * 0.64f
                );
            }
        }
        Iterator<Map.Entry<Zombie, PamActor>> iterator = renderedPlantZombieHeads.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Zombie, PamActor> entry = iterator.next();
            if (!currentLevel.getActiveZombies().contains(entry.getKey())) {
                entry.getValue().remove();
                iterator.remove();
            }
        }
    }

    public boolean isMiniGameTileInteractive() {
        return currentLevel instanceof VaseBreaker || currentLevel instanceof WallNutBowling
            || currentLevel instanceof IZombie || currentLevel instanceof Beghouled;
    }

    public void handleMiniGameTileClick(Tile tile) {
        if (tile == null) return;
        if (currentLevel instanceof VaseBreaker gameMode) {
            if (gameMode.hasUnbrokenVaseAt(tile.getColumn(), tile.getRow())) {
                showMessage(gameMode.breakVaseAt(tile.getColumn(), tile.getRow()));
                plantSelectionController.cancelSelection();return;}
            if (plantSelectionController.hasSelectedPlant()) {
                plantSelectionController.setHoveredTile(tile);
                String error = plantSelectionController.tryPlaceSelectedPlant();if (error != null) showMessage(error);}
            return;}
        if (currentLevel instanceof WallNutBowling gameMode) {
            if (tile.getColumn() > gameMode.getRedLineCoordinateX() - 1) {
                showMessage("Place nuts in the first two columns");return;}
            showMessage(gameMode.executePlaceNutFromBelt(tile.getRow()));return;}
        if (currentLevel instanceof IZombie gameMode) {
            if (MiniGameController.isPlantsPlayer()) {
                if (!plantSelectionController.hasSelectedPlant()) {
                    showMessage("Select a plant first");
                    return;
                }
                plantSelectionController.setHoveredTile(tile);
                String error = MiniGameController.placePlant(plantSelectionController);
                if (error != null) {
                    showMessage(error);
                }
                return;
            }
            if (MiniGameController.isZombiePlayer()) {
                if (selectedMiniZombie == null) {
                    showMessage("Select a zombie first");
                    return;
                }
                String error = MiniGameController.placeZombie(selectedMiniZombie, tile.getColumn(), tile.getRow());
                if (error != null) {
                    showMessage(error);

                } else {
                    selectedMiniZombie = null;
                }
                return;
            }showMessage("Your multiplayer role could not be determined.");return;
        }
        if (currentLevel instanceof Beghouled gameMode) {
            if (selectedBeghouledTile == null) {
                selectedBeghouledTile = tile;showMessage("Select an adjacent tile");return;}
            boolean swapped = gameMode.swapPlants(selectedBeghouledTile.getRow() - 1,
                selectedBeghouledTile.getColumn() - 1, tile.getRow() - 1, tile.getColumn() - 1);
            selectedBeghouledTile = null;showMessage(swapped ? "Match!" : "Invalid swap");
        }
    }

    public Tile getSelectedBeghouledTile() {
        return selectedBeghouledTile;
    }

    private int getDisplayedSunAmount() {
        if (currentLevel instanceof IZombie gameMode) return gameMode.getSunAmount();
        return currentLevel == null ? 0 : currentLevel.getCollectedSunsAmount();
    }

    private String getMiniGameMission() {
        if (currentLevel instanceof VaseBreaker) return "Break every vase and defeat all zombies";
        if (currentLevel instanceof WallNutBowling) return "Roll nuts and defeat every zombie";
        if (currentLevel instanceof IZombie) return "Eat one brain in every lane";
        if (currentLevel instanceof Beghouled gameMode) return "Make " + gameMode.getTargetMatches() + " matches";
        if (currentLevel instanceof Zombotany) return "Defeat the plant-powered zombies";
        return "Complete the minigame";
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
        resultOverlay = new Table();resultOverlay.setFillParent(true);resultOverlay.setTouchable(Touchable.enabled);
        resultOverlay.addListener(new InputListener() {@Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {return true;}
        });
        boolean won = App.getCurrentUser() != null && App.getCurrentUser().isVictroy();
        BorderedTable panel = new BorderedTable();
        boolean miniGame = currentLevel instanceof MiniGame;
        Label title = new Label(won ? (miniGame ? "MINIGAME COMPLETE" : "LEVEL COMPLETE") : "GAME OVER", skin, "big");
        Label text = new Label(won ? (miniGame ? "Minigame completed." : "The lawn is safe.")
            : (miniGame ? "Minigame failed." : "The zombies reached your house."), skin, "default");
        TextButton primary = new TextButton(won ? "CONTINUE" : "RETRY", skin);
        TextButton replay = new TextButton("REPLAY", skin);
        TextButton exit = new TextButton(miniGame ? "MENU" : "EXIT", skin);
        panel.add(title).pad(10f).row();
        panel.add(text).pad(8f).row();
        panel.add(primary).width(180f).pad(6f).row();
        if (won) panel.add(replay).width(180f).pad(6f).row();
        panel.add(exit).width(180f).pad(6f).row();
        resultOverlay.add(panel);
        stage.addActor(resultOverlay);
        primary.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (won) exitGame();else restartGame();}
        });
        replay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                restartGame();
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
        if (currentLevel instanceof MiniGame) return false;
        for (Lawnmower lawnmower : currentLevel.getGameMap().getLawnmowers()) {
            if (lawnmower.isTriggered() && !lawnmower.hasBeenUsed()) {
                return true;
            }
        }
        return false;
    }

    public void updateLawnmowerActors() {
        if (currentLevel instanceof MiniGame) return;
        for (Lawnmower lawnmower : currentLevel.getGameMap().getLawnmowers()) {

            if (lawnmower.hasBeenUsed()) {
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
        if (currentLevel instanceof MiniGame) {
            MiniGame restarted = MiniGameController.restartCurrentMiniGame();
            if (restarted != null) {
                App.setCurrentMenu(Menu.GAME_MANAGER);
                state = GameState.RUNNING;
                game.setScreen(new PlayScreen(game));
            }
            return;
        }
        List<String> chosenPlants = new ArrayList<>(currentLevel.getChosenPlants());
        Level newLevel = new Level(currentLevel.getData());
        newLevel.setCurrentSeason(currentLevel.getCurrentSeason());
        for (String plant : chosenPlants) newLevel.addChosenPlant(plant);
        GameManagerController.getInstance().setCurrentLevel(newLevel);
        if (newLevel.getCurrentSeason() != null) newLevel.getCurrentSeason().levelStarted(newLevel);
        App.setCurrentMenu(Menu.GAME_MANAGER);
        state = GameState.RUNNING;
        game.setScreen(new PlayScreen(game));
    }

    public void exitGame() {
        Gdx.input.setInputProcessor(stage);
        if (currentLevel instanceof MiniGame) {
            App.setCurrentMenu(Menu.MINIGAMES);
            game.setScreen(new MiniGamesScreen(game));
        } else {
            App.setCurrentMenu(Menu.GAME_MENU);
            game.setScreen(new GameMenuScreen(game));
        }
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
