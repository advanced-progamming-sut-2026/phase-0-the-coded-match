package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.audio.MusicManager;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.GameSettings;
import PvZ2.APproject.views.menus.MainMenu;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class SettingsScreen extends BaseScreen {
    private final Main game;

    public SettingsScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        super.show();
        addAssetBackground("OUR_ASSETS/menus/settings_menu.jpg");
        addCurrencyBar();
        addBackButton(() -> {
            App.setCurrentMenu(Menu.MAIN_MENU);
            game.setScreen(new MainMenu(game));
        });

        GameSettings settings = GameSettings.getInstance();
        Table settingsTable = new Table();
        settingsTable.setBounds(330f, 60f, 620f, 468f);
        settingsTable.top();
        stage.addActor(settingsTable);

        addNumberRow(settingsTable, "DIFFICULTY", 5, settings.getGameDifficulty(), settings::setGameDifficulty, 78f);
        addNumberRow(settingsTable, "GAME SPEED", 3, settings.getGameSpeed(), value -> {
            settings.setGameSpeed(value);
            return "";
        }, 78f, 14f);
        addToggleRow(settingsTable, "LAWN GRID", settings.isShowGrid(), enabled -> settings.setShowGrid(enabled), 72f);
        addToggleRow(settingsTable, "DEBUG MODE", settings.isDebugMode(), enabled -> {
            settings.setDebugMode(enabled);
            addCurrencyBar();
        }, 72f);
        addVolumeRow(settingsTable, "MUSIC", settings.getMusicVolume(), value -> {
            settings.setMusicVolume(value);
            MusicManager.updateVolume();
        }, 84f);
        addVolumeRow(settingsTable, "SOUND EFFECTS", settings.getSoundEffectsVolume(),
            settings::setSoundEffectsVolume, 84f);
    }

    private void addNumberRow(Table table, String title, int max, int selected, IntSetting setting, float rowHeight) {
        addNumberRow(table, title, max, selected, setting, rowHeight, 0f);
    }

    private void addNumberRow(Table table, String title, int max, int selected, IntSetting setting,
                              float rowHeight, float topPad) {
        Label label = new Label(title, skin, "medium_outline");
        label.setAlignment(Align.left);
        Table buttons = new Table();
        TextButton[] items = new TextButton[max];
        for (int i = 1; i <= max; i++) {
            final int value = i;
            TextButton button = new TextButton(Integer.toString(i), skin, i == selected ? "purple" : "default");
            items[i - 1] = button;
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setting.set(value);
                    updateSelection(items, value - 1);
                }
            });
            buttons.add(button).size(54, 42).padLeft(6);
        }
        float contentShift = topPad / 2f;
        table.add(label).width(220).left().padLeft(20)
            .padTop(contentShift).padBottom(-contentShift);
        table.add(buttons).right().expandX().padRight(20)
            .padTop(contentShift).padBottom(-contentShift);
        table.row().height(rowHeight);
    }

    private void addToggleRow(Table table, String title, boolean currentValue, BooleanConsumer setter, float rowHeight)
    {
        Label label = new Label(title, skin, "medium_outline");
        label.setAlignment(Align.left);

        TextButton on = new TextButton("ON", skin, currentValue ? "purple" : "default");
        TextButton off = new TextButton("OFF", skin, currentValue ? "default" : "purple");
        Table controls = new Table();
        controls.add(on).size(74, 38).padRight(6);
        controls.add(off).size(74, 38);

        on.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setter.accept(true);
                setToggleStyles(on, off, true);
            }
        });
        off.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setter.accept(false);
                setToggleStyles(on, off, false);
            }
        });

        table.add(label).width(220).left().padLeft(20);
        table.add(controls).right().expandX().padRight(20);
        table.row().height(rowHeight);
    }

    private void addVolumeRow(Table table, String title, int currentValue, IntConsumer setter, float rowHeight) {
        Label label = new Label(title, skin, "medium_outline");
        Slider slider = new Slider(0, 100, 1, false, skin);
        slider.setValue(currentValue);
        Label value = new Label(currentValue + "%", skin, "default");
        value.setAlignment(Align.center);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int amount = Math.round(slider.getValue());
                setter.accept(amount);
                value.setText(amount + "%");
            }
        });
        Table control = new Table();
        control.add(slider).width(235).height(34);
        control.add(value).width(60).padLeft(10);
        table.add(label).width(220).left().padLeft(20);
        table.add(control).right().expandX().padRight(20);
        table.row().height(rowHeight);
    }

    private void updateSelection(TextButton[] buttons, int selectedIndex) {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setStyle(skin.get(i == selectedIndex ? "purple" : "default", TextButton.TextButtonStyle.class));
        }
    }

    private void setToggleStyles(TextButton on, TextButton off, boolean enabled) {
        on.setStyle(skin.get(enabled ? "purple" : "default", TextButton.TextButtonStyle.class));
        off.setStyle(skin.get(enabled ? "default" : "purple", TextButton.TextButtonStyle.class));
    }

    private interface IntSetting {
        String set(int value);
    }

    private interface IntConsumer {
        void accept(int value);
    }

    private interface BooleanConsumer {
        void accept(boolean value);
    }
}
