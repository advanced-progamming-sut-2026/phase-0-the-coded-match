package PvZ2.APproject.views.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import pvz.libpvz.pam.PamPlayer;

import java.util.List;

public class SunHudActor extends Actor {
    private static final String PAM_PATH = "768/INITIAL/EFFECTS/SUN/SUN.PAM";
    private final PamPlayer player;
    private String clipName;
    private float stateTime;
    private float scale = 0.45f;

    public SunHudActor(PamPlayer player) {
        this.player = player;
        setTouchable(Touchable.disabled);
        setSize(58f, 58f);
        try {
            player.loadSync(PAM_PATH);
            List<String> clips = player.clips(PAM_PATH);
            if (clips != null && !clips.isEmpty()) {
                clipName = clips.get(0);
                for (String clip : clips) {
                    if (clip.equalsIgnoreCase("animation") || clip.toLowerCase().contains("animation")) {
                        clipName = clip;
                        break;
                    }
                }
                Rectangle bounds = player.bounds(PAM_PATH, clipName);
                if (bounds != null && bounds.width > 0f && bounds.height > 0f) {
                    scale = Math.min(getWidth() * 0.8f / bounds.width, getHeight() * 0.8f / bounds.height);
                }
            }
        } catch (RuntimeException ignored) {
            clipName = null;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (clipName == null) return;
        float centerX = getX() + getWidth() * 0.5f;
        float centerY = getY() + getHeight() * 0.5f;
        float drawX = centerX;
        float drawY = centerY;
        try {
            Rectangle bounds = player.bounds(PAM_PATH, clipName);
            if (bounds != null) {
                drawX -= (bounds.x + bounds.width * 0.5f) * scale;
                drawY -= (bounds.y + bounds.height * 0.5f) * scale;
            }
            player.draw(batch, PAM_PATH, clipName, stateTime, drawX, drawY, scale, scale, true);
        } catch (RuntimeException ignored) {
        }
    }
}
