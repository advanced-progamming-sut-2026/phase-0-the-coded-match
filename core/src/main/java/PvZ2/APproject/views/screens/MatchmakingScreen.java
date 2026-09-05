package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.controllers.MatchmakingController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import pvz.skin.BorderedTable;

public class MatchmakingScreen extends BaseScreen {
    private final Main game;
    private Label message;

    public MatchmakingScreen(Main game) { this.game = game; }

    @Override
    public void show() {
        super.show();
        background = textures.region("IMAGE_MAINMENU_BACKGROUND");
        backgroundImage = new Image(new TextureRegionDrawable(background));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        BorderedTable table = new BorderedTable();
        Label title = new Label("Online I-Zombie", skin, "big");
        SelectBox<String> stageBox = new SelectBox<>(skin);
        stageBox.setItems("1", "2", "3");
        TextButton random = new TextButton("Find Random Player", skin, "default");
        TextField username = new TextField("", skin, "default");
        username.setMessageText("Opponent Username");
        TextButton invite = new TextButton("Invite Player", skin, "default");
        message = new Label("", skin, "default");

        table.add(title).row();
        table.add(stageBox).width(200).row();
        table.add(random).width(240).row();
        table.add(username).width(240).row();
        table.add(invite).width(240).row();
        table.add(message).width(350).row();
        table.pack();
        table.setPosition((VIRTUAL_WIDTH - table.getWidth()) / 2f, (VIRTUAL_HEIGHT - table.getHeight()) / 2f);
        stage.addActor(table);

        random.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                message.setText(MatchmakingController.findRandom(Integer.parseInt(stageBox.getSelected())));
            }
        });
        invite.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                message.setText(MatchmakingController.findPlayer(username.getText(),
                    Integer.parseInt(stageBox.getSelected())));
            }
        });

        MatchmakingController.setInvitationListener(response -> Gdx.app.postRunnable(()
            -> showInvitation(response)));
        PvZ2.APproject.controllers.MiniGameController.setMatchFoundListener(response ->
            game.setScreen(new PlayScreen(game)));

        addBackButton(() -> {
            MatchmakingController.clearInvitationListener();
            PvZ2.APproject.controllers.MiniGameController.clearMatchFoundListener();
            App.setCurrentMenu(Menu.GAME_MENU);
            game.setScreen(new GameMenuScreen(game));
        });
    }

    private void showInvitation(Response response) {
        Dialog dialog = new Dialog("Match Invitation", skin) {
            @Override
            protected void result(Object object) {
                boolean accepted = Boolean.TRUE.equals(object);
                String invitationId = response.get("invitationId");
                String result = accepted ? MatchmakingController.accept(invitationId) :
                    MatchmakingController.reject(invitationId);
                message.setText(result);
            }
        };
        dialog.text(response.getMessage());
        dialog.button("Accept", true);
        dialog.button("Reject", false);
        dialog.show(stage);
    }
}
