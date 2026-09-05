package PvZ2.APproject.views.actors;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.SunType;
import PvZ2.APproject.models.Sun;
import PvZ2.APproject.models.PlantFood;
import PvZ2.APproject.views.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import pvz.libpvz.pam.PamPlayer;

import java.util.List;

public class SunActor extends Group {
    private final Sun sun;
    private final PlayScreen playScreen;
    private final PamPlayer player;
    private SunType lastSunType;
    private boolean collected;
    private String pamPath;
    private String clipName;
    private float scale = 0.78f;
    private final TextureRegion plantFoodTexture;

    public SunActor(Sun sun, PlayScreen playScreen, PamPlayer player, TextureRegion plantFoodTexture) {
        this.sun = sun;
        this.playScreen = playScreen;
        this.player = player;
        this.plantFoodTexture = plantFoodTexture;
        this.lastSunType = sun.getType();
        setSize(72f, 72f);
        loadAnimation();
    }

    public boolean mouseIsTouching() {
        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        getStage().screenToStageCoordinates(mouse);

        return mouse.x >= getX()
            && mouse.x <= getX() + getWidth()
            && mouse.y >= getY()
            && mouse.y <= getY() + getHeight();
    }

    private void loadAnimation() {
        if (sun instanceof PlantFood) {
            pamPath = null;
            clipName = null;
            return;
        }
        pamPath = sun.getType() == SunType.RADIOACTIVE
            ? "768/FULL/EFFECTS/SUN_BOMB/SUN_BOMB.PAM"
            : "768/INITIAL/EFFECTS/SUN/SUN.PAM";
        String preferred = switch (sun.getType()) {
            case SPECIAL -> "blue";
            case NORMAL, RADIOACTIVE -> "animation";
        };
        try {
            player.loadSync(pamPath);
            List<String> clips = player.clips(pamPath);
            if (clips == null || clips.isEmpty()) {
                clipName = null;
                return;
            }
            clipName = clips.get(0);
            for (String clip : clips) {
                if (clip.equalsIgnoreCase(preferred) || clip.toLowerCase().contains(preferred.toLowerCase())) {
                    clipName = clip;
                    break;
                }
            }
        } catch (RuntimeException ignored) {
            clipName = null;
        }
    }

    private void updatePosition() {
        float centerX = PlayScreen.BOARD_X + (sun.getX() - 0.5f) * PlayScreen.TILE_WIDTH;
        float groundCenterY = PlayScreen.BOARD_Y + (sun.getY() - 0.5f) * PlayScreen.TILE_HEIGHT;
        float centerY = groundCenterY;
        if (sun.isFalling()) {
            float duration = Math.max(0.01f, sun.getFallDuration());
            float progress = 1f - sun.getTimeToReachGround() / duration;
            progress = Math.max(0f, Math.min(1f, progress));
            float startY = 730f;
            centerY = startY + (groundCenterY - startY) * progress;
        }
        setPosition(centerX - getWidth() * 0.5f, centerY - getHeight() * 0.5f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updatePosition();
        if (mouseIsTouching()) {
            collectSun();
        }

        if (lastSunType != sun.getType()) {
            lastSunType = sun.getType();
            loadAnimation();
        }
    }

    public void collectSun() {
        if (collected) return;
        if (GameManagerController.getInstance().getCurrentLevel() == null
            || !GameManagerController.getInstance().getCurrentLevel().getActiveSuns().contains(sun)) {
            collected = true;
            playScreen.removeSunActor(sun);
            return;
        }
        collected = true;
        GameManagerController.getInstance().collectSun(sun);
        playScreen.removeSunActor(sun);
        playScreen.updateSunAmountLabel();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (sun instanceof PlantFood) {
            if (plantFoodTexture != null) batch.draw(plantFoodTexture, getX() + 8f, getY() + 8f, 56f, 56f);
            return;
        }
        if (pamPath == null || clipName == null) return;
        float centerX = getX() + getWidth() * 0.5f;
        float centerY = getY() + getHeight() * 0.5f;
        float drawX = centerX;
        float drawY = centerY;
        try {
            Rectangle bounds = player.bounds(pamPath, clipName);
            if (bounds != null) {
                drawX -= (bounds.x + bounds.width * 0.5f) * scale;
                drawY -= (bounds.y + bounds.height * 0.5f) * scale;
            }
            player.draw(batch, pamPath, clipName, playScreen.getStateTime(), drawX, drawY, scale, scale, true);
        } catch (RuntimeException ignored) {
        }
    }
}
