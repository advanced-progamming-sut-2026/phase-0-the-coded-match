package PvZ2.APproject.views;

import PvZ2.APproject.Main;
import PvZ2.APproject.enums.ZombieState;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.ZombieArmor;
import PvZ2.APproject.views.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import pvz.libpvz.pam.ClipRef;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ZombieView extends Actor {
    private final Main game;
    private final Zombie zombie;
    private ZombieState currentState;
    private final String currentClip;
    private final String frozenClip;
    private final Map<String, Boolean> armorVisibility = new HashMap<>();
    private List<String> clips;
    private float stateTime;
    private float scale = 0.62f;
//    private static final float VISUAL_X_OFFSET_TILES = 0.18f;
    private boolean removing;
    private String deathClipName;

    public ZombieView(Zombie zombie, Main game) {
        this.game = game;
        this.zombie = zombie;
        this.currentState = zombie.getCurrentState();
        this.currentClip = getCurrentClip();
        this.frozenClip = "768/FULL/EFFECTS/FROSTBITE_ICE_BLOCK_ZOMBIE/FROSTBITE_ICE_BLOCK_ZOMBIE.PAM";
        setSize(PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);
        setTouchable(Touchable.disabled);
        loadAnimation();
    }

    private String getCurrentClip() {
        String zombiePath = zombie.getData().getPath();
        String fullPath = "768/FULL/ZOMBIE/" + zombiePath + "/" + zombiePath + ".PAM";
        String initialPath = "768/INITIAL/ZOMBIE/" + zombiePath + "/" + zombiePath + ".PAM";
        if (Gdx.files.internal("IMAGES/" + fullPath).exists()) return fullPath;
        if (Gdx.files.internal("IMAGES/" + initialPath).exists()) return initialPath;
        return null;
    }

    private void loadAnimation() {
        if (currentClip == null) return;
        try {
            game.getPlayer().loadSync(currentClip);
            try {
                game.getPlayer().loadSync(frozenClip);
            } catch (RuntimeException ignored) {
            }
            clips = game.getPlayer().clips(currentClip);
            deathClipName = findDeathClip();
            scale = 0.62f;
        } catch (RuntimeException ignored) {
            clips = null;
        }
    }

    private String resolveClip(String preferred) {
        if (clips == null || clips.isEmpty()) return null;
        for (String candidate : clips) {
            if (candidate.equalsIgnoreCase(preferred)) return candidate;
        }
        String normalized = preferred.toLowerCase(Locale.ROOT);
        for (String candidate : clips) {
            if (candidate.toLowerCase(Locale.ROOT).contains(normalized)) return candidate;
        }
        if (!preferred.equalsIgnoreCase("walk")) {
            for (String candidate : clips) {
                if (candidate.toLowerCase(Locale.ROOT).contains("walk")) return candidate;
            }
        }
        if (!preferred.equalsIgnoreCase("idle")) {
            for (String candidate : clips) {
                if (candidate.toLowerCase(Locale.ROOT).contains("idle")) return candidate;
            }
        }
        return clips.get(0);
    }

    private String findDeathClip() {
        if (clips == null) return null;
        for (String clip : clips) {
            String normalized = clip.toLowerCase(Locale.ROOT);
            if (normalized.contains("death") || normalized.contains("die")) return clip;
        }
        return null;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        if (zombie.getCurrentState() != currentState) {
            currentState = zombie.getCurrentState();
            stateTime = 0f;
        }

        armorVisibility.clear();
        if (zombie.getArmors() != null) {
            for (ZombieArmor armor : zombie.getArmors()) {
                if (armor != null && armor.getData() != null && armor.getData().getPath() != null) {
                    armorVisibility.put(armor.getData().getPath(), armor.getCurrentHp() > 0);
                }
            }
        }

        setPosition(
            PlayScreen.BOARD_X + (((float) zombie.getX() - 1f) * PlayScreen.TILE_WIDTH),
            PlayScreen.BOARD_Y + (zombie.getY() - 1) * PlayScreen.TILE_HEIGHT
        );
    }

    public void beginRemoval() {
        if (removing) return;
        removing = true;
        stateTime = 0f;
        setTouchable(Touchable.disabled);
        clearActions();
        addAction(Actions.sequence(
            Actions.delay(deathClipName == null ? 0.15f : 0.45f),
            Actions.fadeOut(0.3f),
            Actions.removeActor()
        ));
    }

    public boolean isRemovalComplete() {
        return removing && getStage() == null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (currentClip == null || clips == null || clips.isEmpty()) return;

        float alpha = getColor().a * parentAlpha;
        if (zombie.getIsChilled()) batch.setColor(0.75f, 0.88f, 1f, alpha);
        else batch.setColor(1f, 1f, 1f, alpha);

        String preferred = "walk";
        if (removing && deathClipName != null) preferred = deathClipName;
        else if (currentState == ZombieState.IDLE) preferred = "idle";
        else if (currentState == ZombieState.EATING) preferred = "eat";
        String clipName = resolveClip(preferred);

        float centerX = getX() + getWidth() * 0.5f;
        float centerY = getY() + getHeight() * 0.52f;

        if (zombie.isFrozenInBlock()) {
            try {
                game.getPlayer().draw(batch, frozenClip, "idle", stateTime, centerX, centerY, scale, scale, true);
            } catch (RuntimeException ignored) {
            }
            batch.setColor(Color.WHITE);
            return;
        }

        if (clipName != null) {
            try {
                Rectangle bounds = game.getPlayer().bounds(currentClip, clipName);
                float drawX = centerX;
                float drawY = centerY;
                if (bounds != null) {
                    drawX -= (bounds.x + bounds.width * 0.5f) * scale;
                    drawY -= (bounds.y + bounds.height * 0.5f) * scale;
                }
                ClipRef clipRef = game.getPlayer().getClip(currentClip, clipName);
                game.getPlayer().draw(batch, clipRef, stateTime, drawX, drawY, scale, scale, true, armorVisibility);
            } catch (RuntimeException ignored) {
            }
        }
        batch.setColor(Color.WHITE);
    }
}
