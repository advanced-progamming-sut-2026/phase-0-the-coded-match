package PvZ2.APproject.views.actors;

import PvZ2.APproject.Main;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import pvz.libpvz.pam.PamPlayer;

import java.util.List;
import java.util.Locale;

public class MiniGamePamActor extends Actor {
    private final PamPlayer player;
    private final String pamPath;
    private final String preferredClip;
    private String clip;
    private float stateTime;
    private float scale = 0.5f;
    private boolean loadRequested;
    private boolean ready;

    public MiniGamePamActor(Main game, String pamPath, String preferredClip) {
        this.player = game.getPlayer();
        this.pamPath = pamPath;
        this.preferredClip = preferredClip;
        setTouchable(Touchable.disabled);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!loadRequested) {
            loadRequested = true;
            player.loadAsync(pamPath, this::finishLoading);
        }
        if (!ready || clip == null) return;
        try {
            Rectangle bounds = player.bounds(pamPath, clip);
            if (bounds != null && bounds.width > 0f && bounds.height > 0f && getWidth() > 0f && getHeight() > 0f) {
                scale = Math.min(getWidth() / bounds.width, getHeight() / bounds.height) * 0.9f;
            }
        } catch (RuntimeException ignored) {
        }
        player.draw(batch, pamPath, clip, stateTime, getX() + getWidth() / 2f, getY() +
            getHeight() / 2f, scale, scale, true);
    }

    private void finishLoading() {
        try {
            List<String> clips = player.clips(pamPath);
            if (clips == null || clips.isEmpty()) return;
            clip = chooseClip(clips);
            ready = clip != null;
        } catch (RuntimeException ignored) {
            ready = false;
        }
    }

    private String chooseClip(List<String> clips) {
        if (preferredClip != null) {
            String preferred = preferredClip.toLowerCase(Locale.ROOT);
            for (String candidate : clips) {
                if (candidate.equalsIgnoreCase(preferredClip)) return candidate;
            }
            for (String candidate : clips) {
                if (candidate.toLowerCase(Locale.ROOT).contains(preferred)) return candidate;
            }
        }
        for (String candidate : clips) {
            if (candidate.toLowerCase(Locale.ROOT).contains("idle")) return candidate;
        }
        return clips.get(0);
    }
}
