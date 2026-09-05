package PvZ2.APproject.views;

import PvZ2.APproject.Main;
import PvZ2.APproject.enums.PlantState;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.views.screens.PamActor;
import PvZ2.APproject.views.screens.PlayScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import pvz.libpvz.textures.TextureBank;

import java.util.List;
import java.util.Locale;

public class PlantView extends Actor {
    private final Main game;
    private final Plant plant;
    private final TextureBank textures;
    private PlantState state;
    private float stateTime;
    private final String pamPath;
    private List<String> clips;
    private float scale = 0.56f;
    private Rectangle anchorBounds;
    private boolean removing;
    private int lastHealth;
    private float damageFlash;

    public PlantView(Plant plant, Main game) {
        this.plant = plant;
        this.game = game;
        this.textures = game.getTextures();
        this.state = plant.getCurrentState();
        this.lastHealth = visualHealth();
        this.pamPath = PamActor.resolvePlantPam(
            plant.getData().getId(),
            plant.getData().getName(),
            plant.getData().getDisplayName()
        );
        setSize(PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);
        setTouchable(Touchable.disabled);
        loadAnimation();
    }

    private void loadAnimation() {
        if (pamPath == null) return;
        try {
            game.getPlayer().loadSync(pamPath);
            clips = game.getPlayer().clips(pamPath);
            String fitClip = resolveClip("idle");
            Rectangle bounds = fitClip == null ? null : game.getPlayer().bounds(pamPath, fitClip);
            anchorBounds = bounds;
            if (bounds != null && bounds.width > 0f && bounds.height > 0f) {
                scale = Math.min(PlayScreen.TILE_WIDTH * 0.82f / bounds.width,
                    PlayScreen.TILE_HEIGHT * 0.90f / bounds.height);
            }
        } catch (RuntimeException ignored) {
            clips = null;
            scale = 0.56f;
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
        if (!preferred.equalsIgnoreCase("idle")) {
            for (String candidate : clips) {
                if (candidate.toLowerCase(Locale.ROOT).contains("idle")) return candidate;
            }
        }
        return clips.get(0);
    }

    private String resolveClip(String preferred, String... alternatives) {
        if (clips != null) {
            for (String candidate : clips) if (candidate.equalsIgnoreCase(preferred)) return candidate;
            for (String candidate : clips) if (candidate.toLowerCase(Locale.ROOT).contains(preferred.toLowerCase(Locale.ROOT))) return candidate;
            for (String alternative : alternatives) for (String candidate : clips)
                if (candidate.toLowerCase(Locale.ROOT).contains(alternative.toLowerCase(Locale.ROOT))) return candidate;
            if (!preferred.equalsIgnoreCase("idle")) for (String candidate : clips)
                if (!candidate.toLowerCase(Locale.ROOT).contains("idle")) return candidate;
        }
        return resolveClip(preferred);
    }

    private int visualHealth() {
        return Math.max(0, plant.getCurrentHp()) + Math.max(0, plant.getCoverHp()) + Math.max(0, plant.getIceHP());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        int health = visualHealth();
        if (health < lastHealth) damageFlash = 0.14f;
        lastHealth = health;
        if (damageFlash > 0f) damageFlash = Math.max(0f, damageFlash - delta);
        if (!removing && state != plant.getCurrentState()) {
            state = plant.getCurrentState();
            stateTime = 0f;
        }
        setPosition(
            PlayScreen.BOARD_X + (plant.getX() - 1) * PlayScreen.TILE_WIDTH,
            PlayScreen.BOARD_Y + (plant.getY() - 1) * PlayScreen.TILE_HEIGHT
        );
    }

    public void beginRemoval() {
        if (removing) return;
        removing = true;
        state = plant.getCurrentState();
        if (plant.getData().getName().equalsIgnoreCase("squash")) state = PlantState.ATTACKING;
        else if (state != PlantState.EXPLODING && state != PlantState.DEATH && state != PlantState.ATTACKING) state = PlantState.DEATH;
        stateTime = 0f;
        addAction(Actions.sequence(Actions.delay(state == PlantState.DEATH ? 0.55f : 0.85f), Actions.removeActor()));
    }

    public boolean isRemovalComplete() {
        return removing && getStage() == null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (pamPath == null || clips == null || clips.isEmpty()) return;

        String preferred = "idle";
        if (state == PlantState.SHOOTING || state == PlantState.ATTACKING) preferred = "attack";
        else if (state == PlantState.EXPLODING) preferred = "explode";
        else if (state == PlantState.DEATH) preferred = "death";
        else if (state == PlantState.PRODUCING) preferred = "produce";
        else if (state == PlantState.HURT) preferred = "hurt";

        String clipName = switch (state) {
            case SHOOTING -> resolveClip(preferred, "shoot", "action");
            case ATTACKING -> resolveClip(preferred, "action", "shoot");
            case EXPLODING -> resolveClip(preferred, "explosion", "attack");
            case DEATH -> resolveClip(preferred, "die");
            case PRODUCING -> resolveClip(preferred, "sun", "attack");
            case HURT -> resolveClip(preferred, "hit", "damage");
            default -> resolveClip(preferred);
        };
        if (clipName != null) {
            if (damageFlash > 0f) batch.setColor(1f, 0.2f, 0.2f, getColor().a * parentAlpha);
            try {
                float centerX = getX() + getWidth() * 0.5f;
                float groundY = getY() + getHeight() * 0.08f;
                float drawX = centerX;
                float drawY = groundY;
                if (anchorBounds != null) {
                    drawX -= (anchorBounds.x + anchorBounds.width * 0.5f) * scale;
                    drawY -= anchorBounds.y * scale;
                }
                game.getPlayer().draw(batch, pamPath, clipName, stateTime, drawX, drawY, scale, scale, true);
            } catch (RuntimeException ignored) {
            }
            batch.setColor(Color.WHITE);
        }

        if (plant.getFreezeLevel() > 0) {
            batch.setColor(0.7f, 0.9f, 1f, 0.55f);
            TextureRegion iceTexture = null;
            if (plant.isFullyFrozen()) {
                iceTexture = textures.region("IMAGE_EFFECTS_FROSTBITE_ICE_BLOCK_PLANT_FROSTBITE_ICE_BLOCK_PLANT_164X169");
            } else if (plant.getFreezeLevel() == 2) {
                iceTexture = textures.region("IMAGE_EFFECTS_FROSTBITE_ICE_BLOCK_PLANT_FROSTBITE_ICE_BLOCK_PLANT_167X172_3");
            } else if (plant.getFreezeLevel() == 1) {
                iceTexture = textures.region("IMAGE_EFFECTS_FROSTBITE_ICE_BLOCK_PLANT_FROSTBITE_ICE_BLOCK_PLANT_167X172");
            }
            if (iceTexture != null) {
                batch.draw(iceTexture, getX(), getY(), getWidth(), getHeight());
            }
            batch.setColor(Color.WHITE);
        }
    }
}
