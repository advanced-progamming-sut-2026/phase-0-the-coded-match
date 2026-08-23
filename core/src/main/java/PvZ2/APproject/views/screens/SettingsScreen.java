package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.GameSettings;
import PvZ2.APproject.views.menus.MainMenu;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SettingsScreen extends BaseScreen{
    private final Main game;
    private Table settingsTable;

    public SettingsScreen(Main game) {
        this.game = game;
    }


    @Override
    public void show(){
        super.show();

        background = textures.region("IMAGE_MAINMENU_BACKGROUND");
        backgroundImage = new Image(new TextureRegionDrawable(background));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        addBackButton(() -> {
            App.setCurrentMenu(Menu.MAIN_MENU);
            game.setScreen(new MainMenu(game));
        });

        settingsTable = new Table();
        settingsTable.setFillParent(true);
        stage.addActor(settingsTable);
        showSettingsTable();

    }

    private void showSettingsTable() {
        GameSettings settings = GameSettings.getInstance();

        Label title = new Label("SETTINGS", skin);
        title.setFontScale(2.5f);

        Label difficultyLabel = new Label("Game Difficulty:", skin);
        SelectBox<Integer> difficultySelect = new SelectBox<>(skin);
        difficultySelect.setItems(1, 2, 3, 4, 5);
        difficultySelect.setSelected(settings.getGameDifficulty());
        difficultySelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settings.setGameDifficulty(difficultySelect.getSelected());
            }
        });

        Label speedLabel = new Label("Game Speed:", skin);
        SelectBox<Integer> speedSelect = new SelectBox<>(skin);
        speedSelect.setItems(1, 2, 3);
        speedSelect.setSelected(settings.getGameSpeed());
        speedSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settings.setGameSpeed(speedSelect.getSelected());
            }
        });

        CheckBox gridCheckBox = new CheckBox(" Show Lawn Grid", skin);
        gridCheckBox.setChecked(settings.isShowGrid());
        gridCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settings.setShowGrid(gridCheckBox.isChecked());
            }
        });

        CheckBox debugCheckBox = new CheckBox(" Enable Debug Mode", skin);
        debugCheckBox.setChecked(settings.isDebugMode());
        debugCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settings.setDebugMode(debugCheckBox.isChecked());
            }
        });

        settingsTable.add(title).center().row();
        settingsTable.add(difficultyLabel).padRight(30);
        settingsTable.add(difficultySelect).pad(30).row();
        settingsTable.add(speedLabel).padRight(30);
        settingsTable.add(speedSelect).pad(30).row();
        settingsTable.add(gridCheckBox).colspan(6).left().pad(40).row();
        settingsTable.add(debugCheckBox).colspan(6).left().pad(40).row();
    }

}
