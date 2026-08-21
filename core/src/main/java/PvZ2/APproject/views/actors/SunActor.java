package PvZ2.APproject.views.actors;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.SunType;
import PvZ2.APproject.models.Sun;
import PvZ2.APproject.views.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;

public class SunActor extends Group {
    private Sun sun;
    private PlayScreen playScreen;
    private SunType lastSunType;

    private final PamPlayer player;
    private ClipRef sunClip;

    public SunActor(Sun sun, PlayScreen playScreen, PamPlayer player) {
        this.sun = sun;
        this.playScreen = playScreen;
        this.player = player;
        this.lastSunType = sun.getType();

        setSize(120, 120);

        loadAnimation();
    }

    public void loadAnimation() {
        sunClip = null;

        if (sun.getType() == SunType.RADIOACTIVE) {
            String pamPath = "768/FULL/EFFECTS/SUN_BOMB/SUN_BOMB.PAM";
            player.loadSync(pamPath);
            sunClip = player.getClip(pamPath, getClip());
        }
        else if (getClip() != null) {
            String pamPath = "768/INITIAL/EFFECTS/SUN/SUN.PAM";
            player.loadSync(pamPath);
            sunClip = player.getClip(pamPath, getClip());
        }
    }

    public String getClip() {
        switch (sun.getType()) {
            case SunType.NORMAL -> {
                return "animation";
            }
            case SunType.SPECIAL -> {
                return "blue";
            }
            case SunType.RADIOACTIVE -> {
                return "animation";
            }
        }
        return "";
    }

    public void updatePosition() {
        float targetX = playScreen.BOARD_X + sun.getX() * playScreen.TILE_WIDTH;
        float groundY = playScreen.BOARD_Y + sun.getY() * playScreen.TILE_HEIGHT;

        if (sun.isFalling()) {
            float progress = 1f - sun.getTimeToReachGround() / 5f;

            float startY = 768;

            float currentY = startY + (groundY - startY) * progress;

            setPosition(targetX, currentY);
        } else {
            setPosition(targetX, groundY);
        }
    }

    public void updateRadioActiveSunClip() {
        if (lastSunType != sun.getType()) {
            lastSunType = sun.getType();
            loadAnimation();
        }
    }

    public boolean mouseIsTouching() {
        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        getStage().screenToStageCoordinates(mouse);

        return mouse.x >= getX()
            && mouse.x <= getX() + getWidth()
            && mouse.y >= getY()
            && mouse.y <= getY() + getHeight();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        updatePosition();

        if (mouseIsTouching()) {
            collectSun();
        }

        updateRadioActiveSunClip();
    }

    public void collectSun() {
        GameManagerController.getInstance().collectSun(sun);
        playScreen.removeSunActor(sun);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (sunClip != null) {
            player.draw(
                batch,
                sunClip,
                playScreen.getStateTime(),
                getX(),
                getY(),
                true
            );
        }
    }
}
