package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.MiniGameController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import pvz.skin.BorderedTable;

public class MiniGamesScreen extends BaseScreen {
    private final Main game;

    public MiniGamesScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        super.show();
        addMainBackground();
        addCurrencyBar();

        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(70f);

        Label title = new Label("MINI GAMES", skin, "big");
        root.add(title).padBottom(22f).row();

        BorderedTable panel = new BorderedTable();
        addGameRow(panel, "Vasebreaker", "Vasebreaker");
        addGameRow(panel, "Wall-nut Bowling", "WallNutBowling");
        addGameRow(panel, "I, Zombie", "IZombie");
        addGameRow(panel, "Beghouled", "Beghouled");
        addGameRow(panel, "Zombotany", "Zombotany");
        root.add(panel).padTop(8f);
        stage.addActor(root);

        addBackButton(() -> {
            App.setCurrentMenu(Menu.GAME_MENU);
            game.setScreen(new GameMenuScreen(game));
        });
    }

    private void addGameRow(Table panel, String title, String key) {
        Label name = new Label(title, skin, "default");
        panel.add(name).width(190f).left().pad(8f);
        for (int stageNumber = 1; stageNumber <= 3; stageNumber++) {
            final int stage = stageNumber;
            TextButton button = new TextButton("Stage " + stage, skin, "default");
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (MiniGameController.startMinigame(key, stage)) {
                        if (key.equals("IZombie")) {;
                            game.setScreen(new MatchmakingScreen(game));
                            return;
                        }
                        App.setCurrentMenu(Menu.GAME_MANAGER);
                        game.setScreen(new PlayScreen(game));
                    }
                }
            });
            panel.add(button).width(120f).height(44f).pad(6f);
        }
        panel.row();
    }
}
