package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class AuthScreen extends BaseScreen {
    private final Main game;

    public AuthScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        super.show();

        addAssetBackground("OUR_ASSETS/first_pic.png");

        TextButton register = new TextButton("Register", skin, "default");
        TextButton login = new TextButton("Login", skin, "default");
        Table table = new Table();
        table.setFillParent(true);
        table.add(register).width(180).height(50).padBottom(12).row();
        table.add(login).width(180).height(50);
        stage.addActor(table);

        register.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                App.setCurrentMenu(Menu.SIGNUP_MENU);
                game.setScreen(new SignupScreen(game));
            }
        });

        login.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                App.setCurrentMenu(Menu.LOGIN_MENU);
                game.setScreen(new LoginScreen(game));
            }
        });
    }

}
