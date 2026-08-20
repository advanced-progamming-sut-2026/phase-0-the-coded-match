package PvZ2.APproject.views.menus;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.menus.MainMenuController;
import PvZ2.APproject.controllers.menus.NewsMenuController;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.views.screens.BaseScreen;
import PvZ2.APproject.views.screens.SignupScreen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import pvz.skin.BorderedTable;

public class MainMenu extends BaseScreen {
    private final Main game;

    public MainMenu(Main game) {
        this.game = game;
    }

    public static void check(String input) {
        if (input.matches(Commands.LOGOUT.getPattern())) {
            MainMenuController.logout();
        } else {
            System.out.println("invalid command");
        }
    }

    @Override
    public void show() {
        super.show();
        addMainBackground();
        addCurrencyBar();

        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(95);

        Label title = new Label("PLANTS vs. ZOMBIES 2", skin, "medium_outline");
        root.add(title).padBottom(42).row();

        BorderedTable menu = new BorderedTable();
        TextButton playButton = new TextButton("PLAY", skin, "purple");
        TextButton collectionButton = new TextButton("COLLECTION", skin, "default");
        int unread = NewsMenuController.getUnreadCount();
        TextButton newsButton = new TextButton(unread > 0 ? "NEWS  !  " + unread : "NEWS", skin, "default");
        TextButton logoutButton = new TextButton("LOGOUT", skin, "default");

        menu.add(playButton).width(270).height(62).pad(9).row();
        menu.add(collectionButton).width(270).height(52).pad(7).row();
        menu.add(newsButton).width(270).height(52).pad(7).row();
        menu.add(logoutButton).width(270).height(48).pad(7);

        root.add(menu);
        stage.addActor(root);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                App.setCurrentMenu(Menu.CHOOSEPLANTS_MENU);
                game.setScreen(new ChoosePlantsMenu(game));
            }
        });

        collectionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                App.setCurrentMenu(Menu.COLLECTION_MENU);
                game.setScreen(new CollectionMenu(game));
            }
        });

        newsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                App.setCurrentMenu(Menu.NEWS_MENU);
                game.setScreen(new NewsMenu(game));
            }
        });

        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MainMenuController.logout();
                game.setScreen(new SignupScreen(game));
            }
        });
    }
}
