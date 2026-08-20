package PvZ2.APproject.views.menus;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.menus.CollectionMenuController;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.enums.PlantCategory;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;
import PvZ2.APproject.models.plants.PlantUpgradeData;
import PvZ2.APproject.models.zombies.ZombieData;
import PvZ2.APproject.models.zombies.ZombieRepository;
import PvZ2.APproject.views.screens.BaseScreen;
import PvZ2.APproject.views.screens.PamActor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import pvz.skin.BorderedTable;

import java.util.ArrayList;
import java.util.List;

public class CollectionMenu extends BaseScreen {
    private final Main game;
    private Table listTable;
    private BorderedTable detailsTable;
    private Table filtersTable;
    private ScrollPane listScroll;
    private Label messageLabel;
    private boolean showingPlants = true;
    private SelectBox<String> categoryFilter;
    private SelectBox<String> ownershipFilter;
    private SelectBox<String> upgradeFilter;

    public CollectionMenu(Main game) {
        this.game = game;
    }

    public static void check(String input) {
        if (input.matches(Commands.COLLECTION_SHOW_PLANTS.getPattern())) {
            System.out.print(CollectionMenuController.showAchievedPlants());
        } else if (input.matches(Commands.COLLECTION_SHOW_ALL_PLANTS.getPattern())) {
            System.out.print(CollectionMenuController.showAllPlants());
        } else if (input.matches(Commands.COLLECTION_SHOW_ZOMBIES.getPattern())) {
            System.out.print(CollectionMenuController.showSeenZombies());
        } else if (input.matches(Commands.COLLECTION_SHOW_ALL_ZOMBIES.getPattern())) {
            System.out.print(CollectionMenuController.showAllZombies());
        } else if (input.matches(Commands.COLLECTION_SHOW_PLANT.getPattern())) {
            System.out.print(CollectionMenuController.showPlant(input));
        } else if (input.matches(Commands.COLLECTION_SHOW_ZOMBIE.getPattern())) {
            System.out.print(CollectionMenuController.showZombie(input));
        } else if (input.matches(Commands.COLLECTION_UPGRADE.getPattern())) {
            CollectionMenuController.upgradePlant(input);
        } else if (input.matches(Commands.COLLECTION_PURCHASE.getPattern())) {
            CollectionMenuController.purchasePlant(input);
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
        root.top().padTop(66).padLeft(38).padRight(38).padBottom(30);

        Label title = new Label("COLLECTION", skin, "medium_outline");
        root.add(title).colspan(2).padBottom(10).row();

        Table tabs = new Table(skin);
        TextButton plantsTab = new TextButton("PLANTS", skin, "purple");
        TextButton zombiesTab = new TextButton("ZOMBIES", skin, "default");
        tabs.add(plantsTab).width(180).height(44).padRight(8);
        tabs.add(zombiesTab).width(180).height(44);
        root.add(tabs).colspan(2).padBottom(8).row();

        filtersTable = new Table(skin);
        root.add(filtersTable).colspan(2).height(48).left().padBottom(8).row();

        listTable = new Table(skin);
        listTable.top().left();
        listScroll = new ScrollPane(listTable, skin);
        listScroll.setFadeScrollBars(false);
        listScroll.setScrollingDisabled(true, false);

        detailsTable = new BorderedTable();
        detailsTable.top();
        ScrollPane detailsScroll = new ScrollPane(detailsTable, skin);
        detailsScroll.setFadeScrollBars(false);
        detailsScroll.setScrollingDisabled(true, false);

        root.add(listScroll).width(630).height(565).padRight(14);
        root.add(detailsScroll).width(315).height(565);
        stage.addActor(root);

        plantsTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showingPlants = true;
                plantsTab.setStyle(skin.get("purple", TextButton.TextButtonStyle.class));
                zombiesTab.setStyle(skin.get("default", TextButton.TextButtonStyle.class));
                buildPlantFilters();
                rebuildPlantList();
                showDefaultPlantDetails();
            }
        });

        zombiesTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showingPlants = false;
                zombiesTab.setStyle(skin.get("purple", TextButton.TextButtonStyle.class));
                plantsTab.setStyle(skin.get("default", TextButton.TextButtonStyle.class));
                filtersTable.clearChildren();
                rebuildZombieList();
                showDefaultZombieDetails();
            }
        });

        buildPlantFilters();
        rebuildPlantList();
        showDefaultPlantDetails();
    }

    private void buildPlantFilters() {
        filtersTable.clearChildren();

        categoryFilter = new SelectBox<>(skin);
        List<String> categories = new ArrayList<>();
        categories.add("ALL FAMILIES");
        for (PlantCategory category : PlantCategory.values()) {
            categories.add(category.getName());
        }
        categoryFilter.setItems(categories.toArray(new String[0]));

        ownershipFilter = new SelectBox<>(skin);
        ownershipFilter.setItems("ALL", "UNLOCKED", "LOCKED");

        upgradeFilter = new SelectBox<>(skin);
        upgradeFilter.setItems("ANY LEVEL", "UPGRADEABLE", "MAX LEVEL");

        filtersTable.add(new Label("Family", skin, "default")).padRight(5);
        filtersTable.add(categoryFilter).width(170).padRight(12);
        filtersTable.add(new Label("Status", skin, "default")).padRight(5);
        filtersTable.add(ownershipFilter).width(125).padRight(12);
        filtersTable.add(new Label("Upgrade", skin, "default")).padRight(5);
        filtersTable.add(upgradeFilter).width(145);

        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (showingPlants) {
                    rebuildPlantList();
                }
            }
        };
        categoryFilter.addListener(listener);
        ownershipFilter.addListener(listener);
        upgradeFilter.addListener(listener);
    }

    private void rebuildPlantList() {
        listTable.clearChildren();
        int column = 0;

        for (PlantData plant : PlantRepository.getInstance().getAllPlants()) {
            if (!matchesPlantFilters(plant)) {
                continue;
            }

            boolean unlocked = CollectionMenuController.isPlantUnlockedForUi(plant);
            int level = CollectionMenuController.getPlantLevelForUi(plant);
            int seeds = App.getCurrentUser() == null ? 0 : App.getCurrentUser().getSeedPacketCount(plant.getDisplayName());
            PlantUpgradeData next = CollectionMenuController.getNextUpgradeForUi(plant);
            String required = next == null ? "MAX" : Integer.toString(next.getRequiredSeedPackets());

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
            Label state = new Label(
                unlocked
                    ? "Lv " + Math.max(level, 1) + "   Seeds " + seeds + "/" + required
                    : "LOCKED   Seeds " + seeds + "/" + required,
                skin,
                "default"
            );

            card.add(preview).size(105, 78).row();
            card.add(name).width(170).padTop(2).row();
            card.add(state).width(170).padBottom(5);
            card.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showPlantDetails(plant);
                }
            });

            listTable.add(card).width(190).height(145).pad(6);
            column++;
            if (column == 3) {
                listTable.row();
                column = 0;
            }
        }

        if (listTable.getChildren().size == 0) {
            listTable.add(new Label("No plants match these filters", skin, "medium_outline")).pad(35);
        }
        listScroll.layout();
    }

    private boolean matchesPlantFilters(PlantData plant) {
        boolean unlocked = CollectionMenuController.isPlantUnlockedForUi(plant);

        String category = categoryFilter == null ? "ALL FAMILIES" : categoryFilter.getSelected();
        if (!"ALL FAMILIES".equals(category)) {
            if (plant.getCategory() == null || !plant.getCategory().getName().equalsIgnoreCase(category)) {
                return false;
            }
        }

        String ownership = ownershipFilter == null ? "ALL" : ownershipFilter.getSelected();
        if ("UNLOCKED".equals(ownership) && !unlocked) {
            return false;
        }
        if ("LOCKED".equals(ownership) && unlocked) {
            return false;
        }

        String upgrade = upgradeFilter == null ? "ANY LEVEL" : upgradeFilter.getSelected();
        boolean hasNextUpgrade = unlocked && CollectionMenuController.getNextUpgradeForUi(plant) != null;
        boolean canUpgrade = unlocked && CollectionMenuController.canUpgradeForUi(plant);
        if ("UPGRADEABLE".equals(upgrade) && !canUpgrade) {
            return false;
        }
        if ("MAX LEVEL".equals(upgrade) && (!unlocked || hasNextUpgrade)) {
            return false;
        }
        return true;
    }

    private void showPlantDetails(PlantData plant) {
        detailsTable.clearChildren();
        detailsTable.top().pad(12);

        boolean unlocked = CollectionMenuController.isPlantUnlockedForUi(plant);
        int level = CollectionMenuController.getPlantLevelForUi(plant);
        int seeds = App.getCurrentUser() == null ? 0 : App.getCurrentUser().getSeedPacketCount(plant.getDisplayName());
        PlantUpgradeData next = CollectionMenuController.getNextUpgradeForUi(plant);

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

        String seedInfo = next == null ? seeds + " / MAX" : seeds + " / " + next.getRequiredSeedPackets();
        String family = plant.getCategory() == null ? "Unknown" : plant.getCategory().getName();
        String infoText =
            "Level: " + (unlocked ? Math.max(level, 1) : "Locked") + "\n" +
            "Seed packets: " + seedInfo + "\n" +
            "Family: " + family + "\n" +
            "Sun cost: " + plant.getSunCost() + "\n" +
            "Health: " + plant.getBaseHp() + "\n" +
            "Damage: " + plant.getDamage() + "\n" +
            "Recharge: " + plant.getRecharge() + "\n" +
            "Tags: " + plant.getTags();
        Label info = new Label(infoText, skin, "default");
        info.setWrap(true);
        Label description = new Label(plant.getDescription() == null ? "" : plant.getDescription(), skin, "default");
        description.setWrap(true);

        detailsTable.add(name).width(275).padBottom(6).row();
        detailsTable.add(preview).size(225, 150).padBottom(6).row();
        detailsTable.add(info).width(275).left().padBottom(8).row();
        detailsTable.add(description).width(275).left().padBottom(10).row();

        if (!unlocked) {
            TextButton purchase = new TextButton("PURCHASE - " + CollectionMenuController.getPlantPrice() + " COINS", skin, "purple");
            detailsTable.add(purchase).width(265).height(48);
            purchase.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String message = CollectionMenuController.purchasePlantByName(plant.getDisplayName());
                    showMessage(messageLabel, message);
                    rebuildPlantList();
                    showPlantDetails(plant);
                }
            });
        } else if (next != null) {
            TextButton upgrade = new TextButton(
                "UPGRADE - " + next.getRequiredCoins() + " COINS / " + next.getRequiredSeedPackets() + " SEEDS",
                skin,
                "purple"
            );
            detailsTable.add(upgrade).width(265).height(48);
            upgrade.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String message = CollectionMenuController.upgradePlantByName(plant.getDisplayName());
                    showMessage(messageLabel, message);
                    rebuildPlantList();
                    showPlantDetails(plant);
                }
            });
        } else {
            detailsTable.add(new Label("MAX LEVEL", skin, "medium_outline")).pad(10);
        }
    }

    private void showDefaultPlantDetails() {
        detailsTable.clearChildren();
        detailsTable.top().pad(20);
        Label label = new Label("Select a plant to see its details", skin, "medium_outline");
        label.setWrap(true);
        detailsTable.add(label).width(270);
    }

    private void rebuildZombieList() {
        listTable.clearChildren();
        int column = 0;

        for (ZombieData zombie : ZombieRepository.getInstance().getAllZombies()) {
            boolean seen = CollectionMenuController.isZombieSeenForUi(zombie);
            BorderedTable card = new BorderedTable();
            card.setTouchable(seen ? Touchable.enabled : Touchable.disabled);

            if (seen) {
                PamActor preview = new PamActor(
                    game,
                    PamActor.Kind.ZOMBIE,
                    "idle",
                    zombie.getId(),
                    zombie.getDisplayName()
                );
                card.add(preview).size(105, 92).row();
                Label name = new Label(zombie.getDisplayName(), skin, "default");
                name.setWrap(true);
                card.add(name).width(170).padBottom(6);
                card.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        showZombieDetails(zombie);
                    }
                });
            } else {
                card.add(new Label("", skin, "default")).height(120);
            }

            listTable.add(card).width(190).height(145).pad(6);
            column++;
            if (column == 3) {
                listTable.row();
                column = 0;
            }
        }
        listScroll.layout();
    }

    private void showZombieDetails(ZombieData zombie) {
        detailsTable.clearChildren();
        detailsTable.top().pad(12);

        Label name = new Label(zombie.getDisplayName(), skin, "medium_outline");
        name.setWrap(true);
        PamActor preview = new PamActor(
            game,
            PamActor.Kind.ZOMBIE,
            "idle",
            zombie.getId(),
            zombie.getDisplayName()
        );
        String infoText =
            "Health: " + zombie.getHP() + "\n" +
            "Damage: " + zombie.getEatDPS() + "\n" +
            "Speed: " + zombie.getSpeed() + "\n" +
            "Attack interval: " + zombie.getAttackInterval() + "\n" +
            "Wave cost: " + zombie.getWaveCost() + "\n" +
            "Behavior: " + zombie.getBehaviorType() + "\n" +
            "Seasons: " + zombie.getSeasons();
        Label info = new Label(infoText, skin, "default");
        info.setWrap(true);

        detailsTable.add(name).width(275).padBottom(8).row();
        detailsTable.add(preview).size(230, 230).padBottom(8).row();
        detailsTable.add(info).width(275).left();
    }

    private void showDefaultZombieDetails() {
        detailsTable.clearChildren();
        detailsTable.top().pad(20);
        Label label = new Label("Select a discovered zombie to see its details", skin, "medium_outline");
        label.setWrap(true);
        detailsTable.add(label).width(270);
    }
}
