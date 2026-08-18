package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.views.GameMapView;
import PvZ2.APproject.views.ZombieView;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class PlayScreen extends BaseScreen {
    private final Main game;
    private Level currentLevel = GameManagerController.getInstance().getCurrentLevel();
    private GameMapView gameMapView;
    private ZombieView zombieView;

    public PlayScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        super.show();

        gameMapView = new GameMapView(game, currentLevel, textures);
//        gameMapView = new GameMapView(game, textures);
        
        backgroundImage = new Image(new TextureRegionDrawable(gameMapView.getBackground()));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }
}
