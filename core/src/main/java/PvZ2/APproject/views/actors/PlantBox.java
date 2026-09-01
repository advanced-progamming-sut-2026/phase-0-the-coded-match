package PvZ2.APproject.views.actors;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.controllers.PlantSelectionController;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.PlantData;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import pvz.libpvz.textures.TextureBank;
import pvz.skin.PvzSkin;

public class PlantBox extends Group {
    private final PlantData plantData;
    private final PlantSelectionController selectionController;
    private final Label costLabel;
    private final Label cooldownLabel;
    private TextureRegion background;
    private TextureRegion plantImage;
    private boolean available = true;
    private boolean boosted = false;
    private Skin skin;
    private Stage stage;

    public PlantBox(PlantData plantData, PlantSelectionController selectionController, TextureBank textures, Skin skin,
                    Stage stage) {
        this.plantData = plantData;
        this.selectionController = selectionController;
        this.skin = skin;
        this.stage = stage;
        setSize(100, 120);
        setTouchable(Touchable.enabled);
        createVisuals(textures);

        costLabel = new Label(Integer.toString(plantData.getSunCost()), skin, "default");
        costLabel.setFontScale(0.85f);
        costLabel.setPosition(8f, 6f);
        addActor(costLabel);

        Image sun = new Image(new TextureRegionDrawable(
            textures.region("IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN")
        ));
        sun.setSize(18f, 18f);
        sun.setPosition(30f, 7f);
        addActor(sun);

        cooldownLabel = new Label("", skin, "default");
        cooldownLabel.setFontScale(0.75f);
        cooldownLabel.setPosition(58f, 76f);
        addActor(cooldownLabel);
        addClickListener();
    }

    public void createVisuals(TextureBank textures) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        int seasonId = 0;
        if (level != null && level.getCurrentSeason() != null && level.getCurrentSeason().getData() != null) {
            seasonId = level.getCurrentSeason().getData().getId();
        }
        for (String plantId : App.getCurrentUser().getGreenHouse().getStoredBoosts().keySet()) {
            if (plantData.getId().equalsIgnoreCase(plantId)) {
                background = textures.region("IMAGE_UI_PACKETS_BOOST");
                boosted = true;
            }
        }
        if (!boosted) {
            background = switch (seasonId) {
                case 1 -> textures.region("IMAGE_UI_PACKETS_EGYPT");
                case 2 -> textures.region("IMAGE_UI_PACKETS_ICEAGE");
                case 3 -> textures.region("IMAGE_UI_PACKETS_BEACH");
                case 4 -> textures.region("IMAGE_UI_PACKETS_DARK");
                default -> textures.region("IMAGE_UI_PACKETS_PIRATE");
            };
        }
        if (plantData.getId().equalsIgnoreCase("sunflower_twin")) {
            plantImage = textures.region("IMAGE_UI_PACKETS_TWINSUNFLOWER");
        } else {
            plantImage = textures.region("IMAGE_UI_PACKETS_" + plantData.getId().toUpperCase());
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        Level level = GameManagerController.getInstance().getCurrentLevel();
        int cooldown = GameManagerController.getInstance().getPlantCooldowns()
            .getOrDefault(plantData.getName().toLowerCase(), 0);
        available = level != null && level.getCollectedSunsAmount() >= plantData.getSunCost()
            && (GameManagerController.getInstance().isCooldownRemoved() || cooldown <= 0);
        if (!GameManagerController.getInstance().isCooldownRemoved() && cooldown > 0) {
            cooldownLabel.setText((int) Math.ceil(cooldown / 10.0) + "s");
            cooldownLabel.setVisible(true);
        } else {
            cooldownLabel.setVisible(false);
        }
        cooldownLabel.setPosition(Math.max(4f, getWidth() - cooldownLabel.getPrefWidth() - 7f), getHeight() - 22f);
    }

    private void addClickListener() {
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String message = selectionController.selectPlant(plantData);
                if (message != "") {
                    showMessage(message);
                }
            }
        });
    }

    public void showMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        Label messageNotif = new Label("", skin, "promo_ribbon");
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

        stage.addActor(messageNotif);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        boolean selected = selectionController.getSelectedPlant() != null
            && selectionController.getSelectedPlant().getId().equalsIgnoreCase(plantData.getId());
        if (!available) batch.setColor(0.55f, 0.55f, 0.55f, parentAlpha);
        else if (selected) batch.setColor(1f, 1f, 0.78f, parentAlpha);
        else batch.setColor(1f, 1f, 1f, parentAlpha);

        batch.draw(background, getX(), getY(), getWidth(), getHeight());
        float plantSize = Math.min(getWidth() * 0.64f, getHeight() * 0.58f);
        float plantX = getX() + (getWidth() - plantSize) * 0.5f;
        float plantY = getY() + getHeight() * 0.29f;
        if (plantImage != null) {
            batch.draw(plantImage, plantX, plantY, plantSize, plantSize);
        }
        batch.setColor(Color.WHITE);
        super.draw(batch, parentAlpha);
    }
}
