package PvZ2.APproject;

import PvZ2.APproject.controllers.menus.SignupMenuController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.views.menus.MainMenu;
import PvZ2.APproject.views.screens.SignupScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

public class Main extends Game {
    private TextureBank textures;
    private PamPlayer player;

    @Override
    public void create() {
        SignupMenuController.loadFromJson();
        App.initialize();
        App.loadLoggedInUser();


        FileHandle assetsFolder = Gdx.files.internal(".");
        textures = new TextureBank("768", assetsFolder);
        player = new PamPlayer(textures, assetsFolder);

        if (App.getCurrentMenu() == Menu.MAIN_MENU && App.getCurrentUser() != null) {
            setScreen(new MainMenu(this));
        } else {
            setScreen(new SignupScreen(this));
        }
    }

    public TextureBank getTextures() {
        return textures;
    }

    public PamPlayer getPlayer() {
        return player;
    }

    public Main getMain() {
        return this;
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        SignupMenuController.saveToJson();
    }
}
