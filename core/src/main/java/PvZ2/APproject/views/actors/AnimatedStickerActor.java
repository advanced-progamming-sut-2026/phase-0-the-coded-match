package PvZ2.APproject.views.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class AnimatedStickerActor extends Actor {
    private final Animation<TextureRegion> animation;
    private float stateTime;

    public AnimatedStickerActor(String folder) {
        TextureRegion[] frames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            Texture texture = new Texture(
                Gdx.files.internal(folder + "/frame" + i + ".png")
            );
            frames[i] = new TextureRegion(texture);
        }
        animation = new Animation<>(0.12f, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        setSize(140f, 140f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion frame =
            animation.getKeyFrame(stateTime, true);

        batch.draw(frame, getX(), getY(), getWidth(), getHeight());
    }
}
