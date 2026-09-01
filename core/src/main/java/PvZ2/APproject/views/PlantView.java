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

    public PlantView(Plant plant, Main game) {
        this.plant = plant;
        this.game = game;
        this.textures = game.getTextures();
        this.state = plant.getCurrentState();
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

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        if (state != plant.getCurrentState()) {
            state = plant.getCurrentState();
            stateTime = 0f;
        }
        setPosition(
            PlayScreen.BOARD_X + (plant.getX() - 1) * PlayScreen.TILE_WIDTH,
            PlayScreen.BOARD_Y + (plant.getY() - 1) * PlayScreen.TILE_HEIGHT
        );
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

        String clipName = resolveClip(preferred);
        if (clipName != null) {
            try {
                float centerX = getX() + getWidth() * 0.5f;
                float centerY = getY() + getHeight() * 0.5f;
                Rectangle bounds = game.getPlayer().bounds(pamPath, clipName);
                float drawX = centerX;
                float drawY = centerY;
                if (bounds != null) {
                    drawX -= (bounds.x + bounds.width * 0.5f) * scale;
                    drawY -= (bounds.y + bounds.height * 0.5f) * scale;
                }
                game.getPlayer().draw(batch, pamPath, clipName, stateTime, drawX, drawY, scale, scale, true);
            } catch (RuntimeException ignored) {
            }
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
