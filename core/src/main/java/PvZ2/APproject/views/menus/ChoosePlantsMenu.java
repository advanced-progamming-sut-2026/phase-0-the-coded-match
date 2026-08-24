package PvZ2.APproject.views.menus;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.menus.ChoosePlantsMenuController;
import PvZ2.APproject.controllers.menus.CollectionMenuController;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantUpgradeData;
import PvZ2.APproject.views.screens.BaseScreen;
import PvZ2.APproject.views.screens.PamActor;
import PvZ2.APproject.views.screens.PlayScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import pvz.skin.BorderedTable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChoosePlantsMenu extends BaseScreen {
    private final Main game;
    private Table plantGrid;
    private Table selectedTable;
    private BorderedTable detailsTable;
    private Label messageLabel;

    public ChoosePlantsMenu(Main game) {
        this.game = game;
    }

    public static void check(String input) {
        if (input.matches(Commands.CHOOSE_SHOW_ALL.getPattern())) {
            System.out.println(ChoosePlantsMenuController.showAllPlants());
        } else if (input.matches(Commands.CHOOSE_SHOW_AVAILABLE.getPattern())) {
            System.out.println(ChoosePlantsMenuController.showAvailablePlants());
        } else if (input.matches(Commands.CHOOSE_ADD_PLANT.getPattern())) {
            Matcher matcher = Pattern.compile(Commands.CHOOSE_ADD_PLANT.getPattern()).matcher(input);
            matcher.matches();
            System.out.println(ChoosePlantsMenuController.addPlant(matcher.group("type")));
        } else if (input.matches(Commands.CHOOSE_REMOVE_PLANT.getPattern())) {
            Matcher matcher = Pattern.compile(Commands.CHOOSE_REMOVE_PLANT.getPattern()).matcher(input);
            matcher.matches();
            System.out.println(ChoosePlantsMenuController.removePlant(matcher.group("type")));
        } else if (input.matches(Commands.CHOOSE_BOOST_PLANT.getPattern())) {
            Matcher matcher = Pattern.compile(Commands.CHOOSE_BOOST_PLANT.getPattern()).matcher(input);
            matcher.matches();
            System.out.println(ChoosePlantsMenuController.boostPlant(matcher.group("type")));
        } else if (input.matches(Commands.CHOOSE_START_GAME.getPattern())) {
            System.out.println(ChoosePlantsMenuController.startGame());
        } else {
            System.out.println("invalid command");
        }
    }

    @Override
    public void show() {
        super.show();
        addMainBackground();
        addCurrencyBar();
        messageLabel = addMessageLabel();
        addBackButton(() -> {
            App.setCurrentMenu(Menu.MAIN_MENU);
            game.setScreen(new MainMenu(game));
        });

        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(54).padLeft(28).padRight(28).padBottom(18);

        Label title = new Label("CHOOSE YOUR PLANTS", skin, "medium_outline");
        root.add(title).colspan(2).padBottom(8).row();

        BorderedTable selectedWrapper = new BorderedTable();
        selectedWrapper.top().padTop(4);
        selectedTable = new Table(skin);
        selectedWrapper.add(new Label("SELECTED", skin, "medium_outline")).padBottom(2).row();
        selectedWrapper.add(selectedTable).width(920).height(66);
        root.add(selectedWrapper).colspan(2).width(950).height(110).padBottom(8).row();

        plantGrid = new Table(skin);
        plantGrid.top().left();
        ScrollPane scrollPane = new ScrollPane(plantGrid, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        detailsTable = new BorderedTable();
        detailsTable.top();
        ScrollPane detailsScroll = new ScrollPane(detailsTable, skin);
        detailsScroll.setFadeScrollBars(false);
        detailsScroll.setScrollingDisabled(true, false);

        root.add(scrollPane).width(665).height(465).padRight(12);
        root.add(detailsScroll).width(275).height(465).row();

        TextButton startButton = new TextButton("LET'S ROCK!", skin, "purple");
        root.add(startButton).colspan(2).width(240).height(48).padTop(6);
        stage.addActor(root);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String message = ChoosePlantsMenuController.startGame();
                showMessage(messageLabel, message);
                game.setScreen(new PlayScreen(game));
            }
        });

        rebuildSelected();
        rebuildGrid();
        showDefaultDetails();

    }

    private void rebuildSelected() {
        selectedTable.clearChildren();
        List<String> chosen = ChoosePlantsMenuController.getChosenPlantsForUi();
        if (chosen.isEmpty()) {
            Label empty = new Label("No plants selected", skin, "default");
            empty.setColor(Color.DARK_GRAY);
            empty.setFontScale(0.75f);
            selectedTable.add(empty).pad(8);
            return;
        }

        for (String plantName : chosen) {
            TextButton selected = new TextButton(plantName, skin, "purple");
            selectedTable.add(selected).width(108).height(64).pad(3);
            selected.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String message = ChoosePlantsMenuController.removePlant(plantName);
                    showMessage(messageLabel, message);
                    rebuildSelected();
                    rebuildGrid();
                }
            });
        }
    }

    private void rebuildGrid() {
        plantGrid.clearChildren();
        int column = 0;

        for (PlantData plant : ChoosePlantsMenuController.getAvailablePlantsForUi()) {
            boolean unlocked = ChoosePlantsMenuController.isPlantUnlockedForUi(plant.getDisplayName());
            boolean selected = ChoosePlantsMenuController.hasPlantBeenChosen(plant.getDisplayName());
            int level = CollectionMenuController.getPlantLevelForUi(plant);

            BorderedTable card = new BorderedTable();
            card.setTouchable(Touchable.enabled);
            PamActor preview = new PamActor(
                game,
                PamActor.Kind.PLANT,
                "idle",
                plant.getId(),
                plant.getName(),
                plant.getDisplayName()
            );
            Label name = new Label(plant.getDisplayName(), skin, "default");
            name.setWrap(true);
            name.setAlignment(Align.center);
            name.setFontScale(0.72f);
            name.setColor(Color.DARK_GRAY);
            Label state = new Label(
                unlocked
                    ? "Lv " + Math.max(level, 1) + "   " + plant.getSunCost() + " sun" + (selected ? "   SELECTED" : "")
                    : "LOCKED",
                skin,
                "default"
            );
            state.setAlignment(Align.center);
            state.setFontScale(0.64f);
            state.setColor(Color.DARK_GRAY);

            card.add(preview).size(150, 90).padTop(2).row();
            card.add(name).width(185).height(28).row();
            card.add(state).width(185).height(18).padBottom(4);
            card.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showPlantDetails(plant);
                    if (!unlocked) {
                        showMessage(messageLabel, "Plant is locked");
                        return;
                    }
                    if (!ChoosePlantsMenuController.hasCurrentLevel()) {
                        return;
                    }
                    String message;
                    if (ChoosePlantsMenuController.hasPlantBeenChosen(plant.getDisplayName())) {
                        message = ChoosePlantsMenuController.removePlant(plant.getDisplayName());
                    } else {
                        message = ChoosePlantsMenuController.addPlant(plant.getDisplayName());
                    }
                    showMessage(messageLabel, message);
                    rebuildSelected();
                    rebuildGrid();
                }
            });

            plantGrid.add(card).width(205).height(150).pad(5);
            column++;
            if (column == 3) {
                plantGrid.row();
                column = 0;
            }
        }
    }

    private void showPlantDetails(PlantData plant) {
        detailsTable.clearChildren();
        detailsTable.top().pad(10);

        boolean unlocked = ChoosePlantsMenuController.isPlantUnlockedForUi(plant.getDisplayName());
        boolean selected = ChoosePlantsMenuController.hasPlantBeenChosen(plant.getDisplayName());
        int level = CollectionMenuController.getPlantLevelForUi(plant);
        PlantUpgradeData next = CollectionMenuController.getNextUpgradeForUi(plant);
        int seeds = App.getCurrentUser() == null ? 0 : App.getCurrentUser().getSeedPacketCount(plant.getDisplayName());

        Label name = new Label(plant.getDisplayName(), skin, "medium_outline");
        name.setWrap(true);
        PamActor preview = new PamActor(
            game,
            PamActor.Kind.PLANT,
            "idle",
            plant.getId(),
            plant.getName(),
            plant.getDisplayName()
        );
        String family = plant.getCategory() == null ? "Unknown" : plant.getCategory().getName();
        String seedInfo = next == null ? seeds + " / MAX" : seeds + " / " + next.getRequiredSeedPackets();
        Label info = new Label(
            "Level: " + (unlocked ? Math.max(level, 1) : "Locked") + "\n" +
            "Sun cost: " + plant.getSunCost() + "\n" +
            "Health: " + plant.getBaseHp() + "\n" +
            "Damage: " + plant.getDamage() + "\n" +
            "Recharge: " + plant.getRecharge() + "\n" +
            "Family: " + family + "\n" +
            "Seeds: " + seedInfo,
            skin,
            "default"
        );
        info.setWrap(true);
        info.setFontScale(0.76f);
        info.setColor(Color.DARK_GRAY);

        detailsTable.add(name).width(245).padBottom(4).row();
        detailsTable.add(preview).size(225, 150).padBottom(4).row();
        detailsTable.add(info).width(245).left().padBottom(6).row();

        if (unlocked && selected) {
            TextButton boost = new TextButton("BOOST - 2 GEMS", skin, "purple");
            detailsTable.add(boost).width(235).height(42).padBottom(6).row();
            boost.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String message = ChoosePlantsMenuController.boostPlant(plant.getDisplayName());
                    showMessage(messageLabel, message);
                }
            });
        }

        if (unlocked && next != null) {
            TextButton upgrade = new TextButton("UPGRADE", skin, "default");
            detailsTable.add(upgrade).width(235).height(42).padBottom(6).row();
            upgrade.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String message = CollectionMenuController.upgradePlantByName(plant.getDisplayName());
                    showMessage(messageLabel, message);
                    rebuildGrid();
                    showPlantDetails(plant);
                }
            });
        }

        Label description = new Label(plant.getDescription() == null ? "" : plant.getDescription(), skin, "default");
        description.setWrap(true);
        description.setFontScale(0.72f);
        description.setColor(Color.DARK_GRAY);
        detailsTable.add(description).width(245).left();
    }

    private void showDefaultDetails() {
        detailsTable.clearChildren();
        detailsTable.top().pad(20);
        Label label = new Label("Select a plant to view details, boost or upgrade it", skin, "medium_outline");
        label.setWrap(true);
        detailsTable.add(label).width(235);
    }
}
