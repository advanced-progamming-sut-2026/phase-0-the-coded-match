package PvZ2.APproject.views.actors;

import PvZ2.APproject.models.GameMapRelated.Lawnmower;
import PvZ2.APproject.views.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;

public class LawnmowerActor extends Actor {
    private Lawnmower lawnmower;
    private PlayScreen playScreen;

    private PamPlayer player;
    private ClipRef idleClip;

    public LawnmowerActor(Lawnmower lawnmower, PlayScreen playScreen, PamPlayer player) {
        this.lawnmower = lawnmower;
        this.playScreen = playScreen;
        this.player = player;

        setSize(160, 130);

        loadAnimation();

        setPosition(190, playScreen.BOARD_Y + (lawnmower.getRow() * playScreen.TILE_HEIGHT / 2));
    }

    public void loadAnimation() {
        String pamPath = "768/FULL/MOWERS/MOWER_MODERN/MOWER_MODERN.PAM";
        player.loadSync(pamPath);
        idleClip = player.getClip(pamPath, "idle");
    }

    @Override
    public void act(float delta) {
        super.act(delta);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (idleClip != null) {
            player.draw(
                batch,
                idleClip,
                playScreen.getStateTime(),
                getX(),
                getY(),
                true
            );
        }
    }

    public Lawnmower getLawnmower() {
        return lawnmower;
    }
}
