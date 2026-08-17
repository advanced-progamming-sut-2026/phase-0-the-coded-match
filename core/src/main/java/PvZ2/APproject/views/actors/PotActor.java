package PvZ2.APproject.views.actors;

import PvZ2.APproject.controllers.GreenHouseController;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.greenhouse.GreenHousePot;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;

public class PotActor extends Group {
    private GreenHousePot pot;

    private Image potImage;
    private Image lockImage;
    private final PamPlayer player;
    private ClipRef plantClip;
    private float stateTime = 0f;
    private final Label timeLabel;
    private final TextButton growButton;
    private GreenHouseController controller;

    public PotActor(GreenHousePot pot, TextureRegion potRegion, TextureRegion lockRegion, PamPlayer player, Skin skin,
                    GreenHouseController controller) {
        this.pot = pot;
        this.controller = controller;

        potImage = new Image(potRegion);
        potImage.setSize(potRegion.getRegionWidth(), potRegion.getRegionHeight()); //needed?
        addActor(potImage);
        setSize(potRegion.getRegionWidth(), potRegion.getRegionHeight());

        if (pot.isLocked) {
            lockImage = new Image(lockRegion);
            addActor(lockImage);
        } else {
            lockImage = null;
        }

        this.player = player;


        timeLabel = new Label("", skin, "secondary");
        growButton = new TextButton("", skin, "purple");
        addActor(timeLabel);
        addActor(growButton);
        timeLabel.setVisible(false);
        growButton.setVisible(false);

        growButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.growPlant(pot.x, pot.y);
                updateState();
            }
        });

        if (pot.getPlantType() == null) {
            plantClip = null;
        } else {
            String plantName = pot.getPlantType();
            String pamPath = getPamPath(plantName.toUpperCase());
            player.loadSync(pamPath);
            plantClip = player.getClip(pamPath, "idle");
            timeLabel.setVisible(true);
            growButton.setVisible(true);
        }

    }

    public void updateTime() {
        long remainingTime = pot.getRemainingTime();

        long hours = remainingTime / 3600;
        long mins = (remainingTime % 3600) / 60;

        timeLabel.setText(hours + "h " + mins + "m");
    }

    public void updateState() {
        if (pot.getPlantType() == null) {
            timeLabel.setVisible(false);
            growButton.setVisible(false);
            removePlant();
            return;
        }

        if (pot.isReady()) {
            timeLabel.setVisible(false);
            growButton.setVisible(false);
        }
    }

    public void setPlant() {
        String plantId = pot.getPlantType();
        if (plantId != null) {
            String pamPath = getPamPath(plantId.toUpperCase());
            if (pamPath != null) {
                player.loadSync(pamPath);
                plantClip = player.getClip(pamPath, "idle");
            }
            timeLabel.setVisible(true);
            growButton.setVisible(true);
        }
    }

    public void removePlant() {
        plantClip = null;
    }

    private String getPamPath(String plantId) {
        String fullPath = "768/FULL/PLANT/" + plantId + "/" + plantId + ".PAM";
        String initialPath = "768/INITIAL/PLANT/" + plantId + "/" + plantId + ".PAM";

        if (Gdx.files.internal(fullPath).exists()) { //or "assets/" + ?
            return fullPath;
        } else if (Gdx.files.internal(initialPath).exists()){
            return initialPath;
        }

        return null;
    }

    /**plant path: 768/INITIAL/PLANT/MARIGOLD/MARIGOLD.PAM
     * 768/FULL/PLANT/REDSTINGER/REDSTINGER.PAM
     * **/

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        if ("GROWING".equals(pot.status)) {
            if (pot.isReady()) {
                timeLabel.setVisible(false);
                growButton.setVisible(false);
            } else {
                updateTime();
                growButton.setText(pot.getGrowCost() + " Gems");
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (plantClip != null) {
            player.draw(
                batch,
                plantClip,
                stateTime,
                getX(),
                getY(),
                true
            );
        }
    }

    public GreenHousePot getPot() {
        return pot;
    }
}
