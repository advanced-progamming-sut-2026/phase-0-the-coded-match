package PvZ2.APproject.views;

import PvZ2.APproject.Main;
import PvZ2.APproject.enums.PlantState;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.views.screens.PamActor;
import PvZ2.APproject.views.screens.PlayScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import pvz.libpvz.textures.TextureBank;

import java.util.Collections;

public class PlantView extends Actor {
    private final Main game;
    private final Plant plant;
    private TextureBank textures;
    private PlantState state;
    private float stateTime;
    private final String clip;

    public PlantView(Plant plant, Main game) {
        this.plant = plant;
        this.game = game;
        this.textures = game.getTextures();
        this.state = plant.getCurrentState();
        this.clip = PamActor.resolvePlantPam(
                plant.getData().getId(),
                plant.getData().getName(),
                plant.getData().getDisplayName()
        );
    }

    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        if (state != plant.getCurrentState()) {
            state = plant.getCurrentState();
            stateTime = 0;
        }
        setPosition(plant.getX() * 80f, plant.getY() * 100f);
    }

    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        String clipName = "idle";
        if (state == PlantState.SHOOTING || state == PlantState.ATTACKING) clipName = "attack";
        if (state == PlantState.EXPLODING) clipName = "explode";
        if (state == PlantState.DEATH) clipName = "death";
        if (state == PlantState.PRODUCING) clipName = "produce";
        game.getPlayer().draw(batch, clip, clipName, stateTime, getX(), getY(), true, Collections.emptyMap());

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

            batch.draw(iceTexture, getX(), getY(), PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT);
            batch.setColor(Color.WHITE);
        }

    }
}
