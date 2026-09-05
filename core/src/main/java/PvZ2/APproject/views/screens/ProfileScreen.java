package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.menus.ProfileMenuController;
import PvZ2.APproject.models.App;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class ProfileScreen extends BaseScreen {
    private final Main game;
    private final ProfileMenuController controller;
    private Label messageNotif;

    public ProfileScreen(Main game) {
        this.game = game;
        this.controller = new ProfileMenuController();
    }

    @Override
    public void show() {
        super.show();
        addAssetBackground("OUR_ASSETS/menus/profile_menu.jpg");
        addCurrencyBar();
        addBackButton(() -> controller.exit(game));

        Table wrapper = new Table();
        wrapper.setBounds(385, 190, 520, 365);
        wrapper.top();
        stage.addActor(wrapper);

        Table usernameTable = new Table();
        Label username = valueLabel("USERNAME", App.getCurrentUser().getUsername());
        TextButton changeUN = new TextButton("CHANGE", skin, "purple");
        usernameTable.add(username).width(330).left();
        usernameTable.add(changeUN).width(120).height(42);

        Table nicknameTable = new Table();
        Label nickname = valueLabel("NICKNAME", App.getCurrentUser().getNickname());
        TextButton changeNN = new TextButton("CHANGE", skin, "purple");
        nicknameTable.add(nickname).width(330).left();
        nicknameTable.add(changeNN).width(120).height(42);

        wrapper.add(usernameTable).width(470).height(62).row();
        wrapper.add(nicknameTable).width(470).height(62).row();

        Table stats = new Table();
        stats.add(statCard("GAMES PLAYED", App.getCurrentUser().getGamesPlayedCount())).width(145).
            height(90).padRight(10);
        stats.add(statCard("LEVELS", App.getCurrentUser().getLevelsCount())).width(145).height(90).padRight(10);
        stats.add(statCard("MEOW POINTS", App.getCurrentUser().getMeowPoints())).width(145).height(90);
        wrapper.add(stats).padTop(8).padBottom(12).row();

        Table emailTable = new Table();
        Label email = valueLabel("EMAIL", App.getCurrentUser().getEmail());
        TextButton changeE = new TextButton("CHANGE EMAIL", skin, "default");
        emailTable.add(email).width(300).left();
        emailTable.add(changeE).width(150).height(42);
        wrapper.add(emailTable).width(470).height(58).row();

        Table passwordTable = new Table();
        Label password = valueLabel("PASSWORD", "********");
        TextButton changeP = new TextButton("CHANGE PASSWORD", skin, "default");
        passwordTable.add(password).width(300).left();
        passwordTable.add(changeP).width(150).height(42);
        wrapper.add(passwordTable).width(470).height(58);

        messageNotif = addMessageLabel();

        changeUN.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editInformation(usernameTable, username, changeUN, "New Username", "username");
            }
        });

        changeNN.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editInformation(nicknameTable, nickname, changeNN, "New Nickname", "nickname");
            }
        });

        changeE.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editInformation(emailTable, email, changeE, "New Email", "email");
            }
        });

        changeP.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                passwordTable.clearChildren();
                TextField currentPass = new TextField("", skin, "default");
                currentPass.setMessageText("Current Password");
                currentPass.setPasswordMode(true);
                currentPass.setPasswordCharacter('*');
                TextField newPass = new TextField("", skin, "default");
                newPass.setMessageText("New Password");
                newPass.setPasswordMode(true);
                newPass.setPasswordCharacter('*');
                TextButton confirm = new TextButton("CONFIRM", skin, "purple");
                passwordTable.add(currentPass).width(160).height(42).padRight(8);
                passwordTable.add(newPass).width(160).height(42).padRight(8);
                passwordTable.add(confirm).width(120).height(42);
                confirm.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        String message = controller.changePassword(currentPass.getText(), newPass.getText());
                        showProfileMessage(message);
                        passwordTable.clearChildren();
                        passwordTable.add(password).width(300).left();
                        passwordTable.add(changeP).width(150).height(42);
                    }
                });
            }
        });
    }

    private Label valueLabel(String title, String value) {
        Label label = new Label(title + "\n" + (value == null ? "" : value), skin, "medium_outline");
        label.setAlignment(Align.left);
        return label;
    }

    private Table statCard(String title, int value) {
        Table table = new Table();
        Label titleLabel = new Label(title, skin, "default");
        Label valueLabel = new Label(Integer.toString(value), skin, "medium_outline");
        titleLabel.setAlignment(Align.center);
        valueLabel.setAlignment(Align.center);
        table.add(titleLabel).center().row();
        table.add(valueLabel).center().padTop(6);
        return table;
    }

    private void editInformation(Table table, Label label, TextButton button, String hint, String field) {
        table.clearChildren();
        TextField input = new TextField("", skin, "default");
        input.setMessageText(hint);
        TextButton confirm = new TextButton("CONFIRM", skin, "purple");
        table.add(input).width(300).height(42).padRight(10);
        table.add(confirm).width(140).height(42);
        confirm.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String message = switch (field) {
                    case "username" -> controller.changeUsername(input.getText());
                    case "nickname" -> controller.changeNickname(input.getText());
                    case "email" -> controller.changeEmail(input.getText());
                    default -> "Invalid profile field";
                };
                showProfileMessage(message);
                if (field.equals("username")) label.setText("USERNAME\n" + App.getCurrentUser().getUsername());
                if (field.equals("nickname")) label.setText("NICKNAME\n" + App.getCurrentUser().getNickname());
                if (field.equals("email")) label.setText("EMAIL\n" + App.getCurrentUser().getEmail());
                table.clearChildren();
                table.add(label).width(330).left();
                table.add(button).width(field.equals("email") ? 150 : 120).height(42);
            }
        });
    }

    private void showProfileMessage(String message) {
        messageNotif.clearActions();
        messageNotif.setText(message == null ? "" : message);
        messageNotif.setVisible(true);
        messageNotif.pack();
        messageNotif.getColor().a = 1f;
        messageNotif.addAction(Actions.sequence(Actions.delay(2f), Actions.fadeOut(0.5f), Actions.hide()));
    }
}
