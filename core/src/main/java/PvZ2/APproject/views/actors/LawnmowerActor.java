package PvZ2.APproject.views.actors;

import PvZ2.APproject.models.GameMapRelated.Lawnmower;
import PvZ2.APproject.views.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import pvz.libpvz.pam.PamPlayer;

import java.util.List;

public class LawnmowerActor extends Actor {
    private final Lawnmower lawnmower;
    private final PlayScreen playScreen;
    private final PamPlayer player;
    private String pamPath;
    private String clipName;
    private float scale = 0.45f;
    private float stateTime;
    private float moveSpeed = 520f;

    public LawnmowerActor(Lawnmower lawnmower, PlayScreen playScreen, PamPlayer player) {
        this.lawnmower = lawnmower;
        this.playScreen = playScreen;
        this.player = player;
        setSize(82f, PlayScreen.TILE_HEIGHT * 0.9f);
        setTouchable(Touchable.disabled);
        loadAnimation();
        setPosition(
            PlayScreen.BOARD_X - getWidth() + 8f,
            PlayScreen.BOARD_Y + (lawnmower.getRow() - 1) * PlayScreen.TILE_HEIGHT +
                (PlayScreen.TILE_HEIGHT - getHeight()) * 0.5f
        );
    }

    private void loadAnimation() {
        int seasonId = 0;
        if (playScreen.getCurrentLevel() != null
            && playScreen.getCurrentLevel().getCurrentSeason() != null
            && playScreen.getCurrentLevel().getCurrentSeason().getData() != null) {
            seasonId = playScreen.getCurrentLevel().getCurrentSeason().getData().getId();
        }
        pamPath = switch (seasonId) {
            case 1 -> "768/INITIAL/MOWERS/MOWER_EGYPT/MOWER_EGYPT.PAM";
            case 2 -> "768/FULL/MOWERS/MOWER_ICEAGE/MOWER_ICEAGE.PAM";
            case 3 -> "768/FULL/MOWERS/MOWER_BEACH/MOWER_BEACH.PAM";
            case 4 -> "768/FULL/MOWERS/MOWER_DARK/MOWER_DARK.PAM";
            default -> "768/FULL/MOWERS/MOWER_MODERN/MOWER_MODERN.PAM";
        };
        try {
            player.loadSync(pamPath);
            List<String> clips = player.clips(pamPath);
            if (clips == null || clips.isEmpty()) return;
            clipName = clips.get(0);
            for (String clip : clips) {
                if (clip.equalsIgnoreCase("idle") || clip.toLowerCase().contains("idle")) {
                    clipName = clip;
                    break;
                }
            }
            Rectangle bounds = player.bounds(pamPath, clipName);
            if (bounds != null && bounds.width > 0f && bounds.height > 0f) {
                scale = Math.min(getWidth() * 0.9f / bounds.width, getHeight() * 0.9f / bounds.height);
            }
        } catch (RuntimeException ignored) {
            clipName = null;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        if (!lawnmower.isTriggered() || lawnmower.hasBeenUsed()) return;
        moveBy(moveSpeed * delta, 0f);
        float endX = PlayScreen.BOARD_X + playScreen.getCurrentLevel().getGameMap().getColumns() *
            PlayScreen.TILE_WIDTH + getWidth();
        if (getX() >= endX) {
            lawnmower.setHasBeenUsed(true);
            playScreen.removeLawnmowerActor(lawnmower);
        }
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
            Rectangle bounds = player.bounds(pamPath, clipName);
            if (bounds != null) {
                drawX -= (bounds.x + bounds.width * 0.5f) * scale;
                drawY -= (bounds.y + bounds.height * 0.5f) * scale;
            }
        } catch (RuntimeException ignored) {
        }
        try {
            player.draw(batch, pamPath, clipName, stateTime, drawX, drawY, scale, scale, true);
        } catch (RuntimeException ignored) {
        }
    }

    public Lawnmower getLawnmower() {
        return lawnmower;
    }
}
