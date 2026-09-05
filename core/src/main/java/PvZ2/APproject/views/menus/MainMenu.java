package PvZ2.APproject.views.menus;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.menus.MainMenuController;
import PvZ2.APproject.controllers.menus.NewsMenuController;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.views.screens.BaseScreen;
import PvZ2.APproject.views.screens.GameMenuScreen;
import PvZ2.APproject.views.screens.ProfileScreen;
import PvZ2.APproject.views.screens.SettingsScreen;
import PvZ2.APproject.views.screens.AuthScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

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
        addAssetBackground("OUR_ASSETS/menus/main_menu.jpg");addCurrencyBar();
        Label gameTitle = new Label("THE CODED\nMATCH", skin, "medium_outline");
        gameTitle.setAlignment(Align.center);
        gameTitle.setBounds(72f, 334f, 260f, 88f);
        stage.addActor(gameTitle);
        TextButton playButton = createStoneTextButton("PLAY");
        TextButton settingsButton = createStoneTextButton("SETTINGS");
        int unread = NewsMenuController.getUnreadCount();
        TextButton newsButton = createStoneTextButton(unread > 0 ? "NEWS  ! " + unread : "NEWS");
        TextButton profileButton = createStoneTextButton("PROFILE");
        TextButton logoutButton = createStoneTextButton("LOGOUT");
        playButton.setBounds(454, 430, 372, 82);
        settingsButton.setBounds(454, 338, 372, 82);
        newsButton.setBounds(454, 251, 372, 78);
        profileButton.setBounds(454, 170, 372, 74);
        logoutButton.setBounds(92f, 240f, 238f, 66f);
        stage.addActor(playButton);stage.addActor(settingsButton);
        stage.addActor(newsButton);stage.addActor(profileButton);stage.addActor(logoutButton);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                App.setCurrentMenu(Menu.GAME_MENU);game.setScreen(new GameMenuScreen(game));
            }
        });
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                App.setCurrentMenu(Menu.SETTINGS_MENU);game.setScreen(new SettingsScreen(game));
            }
        });
        newsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                App.setCurrentMenu(Menu.NEWS_MENU);game.setScreen(new NewsMenu(game));
            }
        });
        profileButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                App.setCurrentMenu(Menu.PROFILE_MENU);game.setScreen(new ProfileScreen(game));
            }
        });
        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MainMenuController.logout();game.setScreen(new AuthScreen(game));
            }
        });}

    private TextButton createStoneTextButton(String text) {
        TextButton.TextButtonStyle base = skin.get("default", TextButton.TextButtonStyle.class);
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(base);
        style.up = null;
        style.down = null;
        style.over = null;
        style.checked = null;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.valueOf("B6FF5C");
        style.downFontColor = Color.valueOf("74D83B");
        return new TextButton(text, style);
    }
}
