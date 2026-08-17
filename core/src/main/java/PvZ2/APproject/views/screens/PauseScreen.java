package PvZ2.APproject.views.screens;

import PvZ2.APproject.enums.ScreenRelated.GameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PauseScreen extends BaseScreen {
    //todo: enter this screen from play screen, which should have a field called state which show RUNNING or PAUSED,
    // so add this code to create method of play screen
    //set state to paused
    private GameState state = GameState.RUNNING;
    private Stage pauseStage;

    @Override
    public void show() {
        super.show();

        state = GameState.PAUSED;
        Table pauseTable = new Table(skin);
        pauseTable.setFillParent(true);

        TextButton saveAndExitBtn = new TextButton("SAVE AND EXIT", skin);
        TextButton restartBtn = new TextButton("RESTART", skin);
        TextButton resumeBtn = new TextButton("RESUME", skin);

        pauseTable.add(saveAndExitBtn).row();
        pauseTable.add(restartBtn).row();
        pauseTable.add(resumeBtn).row();

        pauseStage.addActor(pauseTable);

        saveAndExitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });

        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });

        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                state = GameState.RUNNING;
                Gdx.input.setInputProcessor(null);
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        Gdx.input.setInputProcessor(pauseStage);
        if (state == GameState.PAUSED) {
            pauseStage.act();
            pauseStage.draw();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
