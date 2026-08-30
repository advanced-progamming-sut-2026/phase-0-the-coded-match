package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.menus.GameMenuController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.LevelData;
import PvZ2.APproject.models.seasons.SeasonData;
import PvZ2.APproject.models.seasons.SeasonRepository;
import PvZ2.APproject.views.menus.ChoosePlantsMenu;
import PvZ2.APproject.views.menus.CollectionMenu;
import PvZ2.APproject.views.menus.ChoosePlantsMenu;
import PvZ2.APproject.views.menus.CollectionMenu;
import PvZ2.APproject.views.menus.MainMenu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import pvz.skin.BorderedTable;

public class GameMenuScreen extends BaseScreen{
    private final Main game;
    private Table containerTable;
    private int currentSelectedIndex = 1;

    public GameMenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show(){
        super.show();

        background = textures.region("IMAGE_MAINMENU_BACKGROUND");
        backgroundImage = new Image(new TextureRegionDrawable(background));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        addCurrencyBar();

        containerTable = new Table();
        containerTable.setFillParent(true);
        stage.addActor(containerTable);
        showChaptersList();

        addBackButton(() -> {
            App.setCurrentMenu(Menu.MAIN_MENU);
            game.setScreen(new MainMenu(game));
        });

        Table screensTable = new Table(skin);

        ImageButton collectionButton = new ImageButton(skin, "almanac");
        ImageButton greenhouseButton = new ImageButton(skin, "hud_zg");
        TextButton leaderboardButton = new TextButton("Leaderboard", skin, "brown");
        TextButton shopButton = new TextButton("Shop", skin, "purple");

        screensTable.add(collectionButton);
        screensTable.add(greenhouseButton);
        screensTable.add(leaderboardButton);
        screensTable.add(shopButton);
        screensTable.pack();
        screensTable.setPosition(100, VIRTUAL_HEIGHT - 80);

        stage.addActor(screensTable);

        collectionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CollectionMenu(game));
                App.setCurrentMenu(Menu.COLLECTION_MENU);
            }
        });

        greenhouseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GreenHouseScreen(game));
                App.setCurrentMenu(Menu.GREEN_HOUSE);
            }
        });

        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LeaderBoardScreen(game));
                App.setCurrentMenu(Menu.LEADERBOARD);
            }
        });

        shopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ShopScreen(game));
                App.setCurrentMenu(Menu.SHOP);
            }
        });
    }

    private void showChaptersList(){
        containerTable.clearChildren();

        Label title = new Label("Select Chapter", skin, "default");
        containerTable.add(title).padBottom(20).row();

        Table chaptersGrid = new Table();
        for(SeasonData szn : SeasonRepository.getInstance().getAllSeasons()){
            boolean isCenter = (szn.getId() == currentSelectedIndex);
            Image chapterImage = null;
            switch(szn.getId()){
                case 1:
                    chapterImage = new Image(textures.region("IMAGE_UI_UNIVERSE_WORLDS_EGYPT"));
                    break;
                case 2:
                    chapterImage = new Image(textures.region("IMAGE_UI_UNIVERSE_WORLDS_ICEAGE"));
                    break;
                case 3:
                    chapterImage = new Image(textures.region("IMAGE_UI_UNIVERSE_WORLDS_BEACH"));
                    break;
                case 4:
                    chapterImage = new Image(textures.region("IMAGE_UI_UNIVERSE_WORLDS_DARK"));
                    break;
            }
            chapterImage.setScaling(Scaling.fit);

            float width = isCenter ? 420f : 300f;
            float height = isCenter ? 440f : 320f;

//            boolean unlocked = false;
//            if(App.getCurrentUser().getLastSeason() != null) {
//                if (szn.getId() <= App.getCurrentUser().getLastSeason().getData().getId()) {
//                    unlocked = true;
//                }
//            }
            boolean unlocked = true;

            Table card = new Table();
            Label nameLabel = new Label(szn.getDisplayName(), skin, "default");
            nameLabel.setFontScale(isCenter ? 1.5f : 1.0f);
            card.add(nameLabel).padTop(10).padBottom(5).row();
            int lvlNum = 0;
            if(App.getCurrentUser().getLastLevel() == null){
                lvlNum = 0;
            }else {
                lvlNum = App.getCurrentUser().getLastLevel().getLevelNumber();
            }
            Label progressLabel = new Label( lvlNum + "/ 4  Completed", skin, "default");
            card.add(progressLabel).padBottom(10).row();//todo: this is for all seasons all at once

            if (!isCenter) {
                chapterImage.setColor(1, 1, 1, 0.5f);
            } else if (!unlocked) {
                chapterImage.setColor(0.7f, 0.7f, 0.7f, 0.8f);
            }
            card.add(chapterImage).size(width, height).row();

            if (isCenter) {
                if (unlocked || szn.getId() == 1) {
                    TextButton enterBtn = new TextButton("ENTER", skin, "default");
                    enterBtn.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            GameMenuController.enterSeason(szn.getName());
                            showListGrid(szn.getId());
                        }
                    });
                    card.add(enterBtn).width(160).height(50).padTop(10).row();
                }else{
                    Label lockedLabel = new Label("LOCKED", skin, "default");
                    lockedLabel.setColor(Color.RED);
                    lockedLabel.setFontScale(1.3f);
                    card.add(lockedLabel).padTop(10).row();
                }
            }

            final int targetId = szn.getId();
            card.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (currentSelectedIndex != targetId) {
                        currentSelectedIndex = targetId;
                        showChaptersList();
                    }
                }
            });

            float padHorizontal = isCenter ? 80f : 40f;
            chaptersGrid.add(card).padLeft(padHorizontal).padRight(padHorizontal).align(Align.bottom);
        }

        ScrollPane scrollPane = new ScrollPane(chaptersGrid, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, true);
        scrollPane.setOverscroll(false, true);
        containerTable.add(scrollPane).width(VIRTUAL_WIDTH).height(VIRTUAL_HEIGHT);
    }

    private void showListGrid(int seasonId){
        containerTable.clearChildren();


        Label title = new Label("Select Level", skin, "big");
        containerTable.add(title).padLeft(500).row();

        Table levelsGrid = new Table();

        for(LevelData ld : SeasonRepository.getInstance().findById(seasonId).getLevels()){
            boolean isCenter = (ld.getLevelNumber() == currentSelectedIndex);
            Image levelImage = null;
            if(seasonId == 1) {
                switch (ld.getLevelNumber()) {
                    case 1:
                        levelImage = new Image(textures.region("IMAGE_WORLDMAP_EGYPT_ISLAND14"));
                        break;
                    case 2:
                        levelImage = new Image(textures.region("IMAGE_WORLDMAP_EGYPT_ISLAND16"));
                        break;
                    case 3:
                        levelImage = new Image(textures.region("IMAGE_WORLDMAP_ZOMBOSS_NODE_EGYPT_ZOMBOSS_NODE_EGYPT_914X994"));
                        break;
                    case 4:
                        levelImage = new Image(textures.region("IMAGE_WORLDMAP_EGYPT_ISLAND3"));
                        break;
                }
            } else if(seasonId == 2){
                switch(ld.getLevelNumber()){
                    case 1:
                        levelImage = new Image(textures.region("IMAGE_WORLDMAP_EGYPT_ISLAND1"));
                        break;
                    case 2:
                        levelImage = new Image(textures.region("IMAGE_WORLDMAP_EGYPT_ISLAND16"));
                        break;
                    case 3:
                        levelImage = new Image(textures.region("IMAGE_WORLDMAP_ZOMBOSS_NODE_EGYPT_ZOMBOSS_NODE_EGYPT_914X994"));
                        break;
                    case 4:
                        levelImage = new Image(textures.region("IMAGE_WORLDMAP_EGYPT_ISLAND3"));
                        break;
                }
            }else if(seasonId == 3){
                switch(ld.getLevelNumber()){
                    case 1:
                        levelImage = new Image(textures.region("IMAGE_WORLDMAP_EGYPT_ISLAND1"));
                        break;
                    case 2:
                        levelImage = new Image(textures.region("IMAGE_WORLDMAP_EGYPT_ISLAND16"));
                        break;
                    case 3:
                        levelImage = new Image(textures.region("IMAGE_WORLDMAP_ZOMBOSS_NODE_EGYPT_ZOMBOSS_NODE_EGYPT_914X994"));
                        break;
                    case 4:
                        levelImage = new Image(textures.region("IMAGE_WORLDMAP_EGYPT_ISLAND3"));
                        break;
                }
            }else if(seasonId == 4){
                switch(ld.getLevelNumber()){
                    case 1:
                        levelImage = new Image(textures.region("IMAGE_WORLDMAP_EGYPT_ISLAND1"));
                        break;
                    case 2:
                        levelImage = new Image(textures.region("IMAGE_WORLDMAP_EGYPT_ISLAND16"));
                        break;
                    case 3:
                        levelImage = new Image(textures.region("IMAGE_WORLDMAP_ZOMBOSS_NODE_EGYPT_ZOMBOSS_NODE_EGYPT_914X994"));
                        break;
                    case 4:
                        levelImage = new Image(textures.region("IMAGE_WORLDMAP_EGYPT_ISLAND3"));
                        break;
                }
            }
            levelImage.setScaling(Scaling.fit);

            float width = isCenter ? 420f : 240f;
            float height = isCenter ? 440f : 260f;

//            boolean unlocked = false;
//            if(App.getCurrentUser().getLastLevel() != null) {
//                if (ld.getLevelNumber() <= App.getCurrentUser().getLastLevel().getLevelNumber()) {
//                    unlocked = true;
//                }
//            }
            boolean unlocked = true;

            Table card = new Table();
            Label nameLabel = new Label(ld.getName(), skin, "default");
            nameLabel.setFontScale(isCenter ? 1.5f : 1.0f);
            card.add(nameLabel).padTop(10).padBottom(5).row();
            card.add(levelImage).size(width, height).row();

            if(isCenter) {
                if (unlocked || ld.getLevelNumber() == 1) {
                    TextButton enterBtn = new TextButton("ENTER", skin, "default");
                    enterBtn.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            GameMenuController.enterLevel(ld.getLevelNumber());
                            game.setScreen(new ChoosePlantsMenu(game));
                        }
                    });
                    card.add(enterBtn).width(160).height(50).padTop(10).row();
                } else {
                    Label lockedLabel = new Label("LOCKED", skin, "default");
                    lockedLabel.setColor(Color.RED);
                    lockedLabel.setFontScale(1.3f);
                    card.add(lockedLabel).padTop(10).row();
                }
            }

            final int targetId = ld.getLevelNumber();
            card.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (currentSelectedIndex != targetId) {
                        currentSelectedIndex = targetId;
                        showListGrid(seasonId);
                    }
                }
            });

            float padHorizontal = isCenter ? 80f : 40f;
            levelsGrid.add(card).padLeft(padHorizontal).padRight(padHorizontal).align(Align.bottom);
        }
        ScrollPane scrollPane = new ScrollPane(levelsGrid, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, true);
        scrollPane.setOverscroll(false, false);
        containerTable.add(scrollPane).height(VIRTUAL_HEIGHT-120).row();

        TextButton backBtn = new TextButton("< Back to Chapters", skin, "default");
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showChaptersList();
            }
        });
        containerTable.add(backBtn).width(220).height(50).right().padRight(20).padTop(5).padBottom(10).row();

    }
}
