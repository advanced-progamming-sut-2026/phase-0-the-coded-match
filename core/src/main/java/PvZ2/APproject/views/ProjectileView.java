package PvZ2.APproject.views;

import PvZ2.APproject.enums.PlantTag;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.views.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import pvz.libpvz.pam.PamPlayer;

import java.util.List;

public class ProjectileView extends Actor {
    private final Projectile projectile;
    private final PlayScreen playScreen;
    private final PamPlayer player;
    private final String pamPath;
    private String clip = "animation";
    private boolean loaded;

    public ProjectileView(Projectile projectile, PlayScreen playScreen, PamPlayer player) {
        this.projectile = projectile;
        this.playScreen = playScreen;
        this.player = player;
        if (projectile.getCreatorPlantCategory() == null) {
            pamPath = "768/FULL/EFFECTS/ZOMBIE_OCTOPUS_PROJECTILE/ZOMBIE_OCTOPUS_PROJECTILE.PAM";
        } else if (projectile.getCreatorPlantCategory().hasThisTag(PlantTag.ICE)) {
            pamPath = "768/INITIAL/EFFECTS/T_SNOW_PEA/T_SNOW_PEA.PAM";
        } else {
            pamPath = "768/INITIAL/EFFECTS/T_PEA_PROJECTILE/T_PEA_PROJECTILE.PAM";
        }
        setTouchable(Touchable.disabled);
        try {
            player.loadSync(pamPath);
            List<String> clips = player.clips(pamPath);
            if (clips != null && !clips.isEmpty() && !clips.contains(clip)) {
                clip = clips.get(0);
            }
            loaded = true;
        } catch (RuntimeException ignored) {
            loaded = false;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setPosition(
            PlayScreen.BOARD_X + ((float) projectile.getxCoordinate() - 1f) *
                PlayScreen.TILE_WIDTH + PlayScreen.TILE_WIDTH * 0.5f,
            PlayScreen.BOARD_Y + ((float) projectile.getyCoordinate() - 1f) *
                PlayScreen.TILE_HEIGHT + PlayScreen.TILE_HEIGHT * 0.5f
        );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (loaded) {
            player.draw(batch, pamPath, clip, playScreen.getStateTime(), getX(), getY(),
                0.45f, 0.45f, true);
        }
    }
}
