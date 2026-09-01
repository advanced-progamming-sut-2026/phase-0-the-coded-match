package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.ShopController;
import PvZ2.APproject.controllers.menus.ChoosePlantsMenuController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.enums.ShopRelated.ShopItemData;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;
import PvZ2.APproject.views.menus.MainMenu;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.ray3k.tenpatch.TenPatchDrawable;

import java.util.ArrayList;
import java.util.List;

public class ShopScreen extends  BaseScreen{
    private final Main game;
    private Table containerTable;

    public ShopScreen(Main game) {
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
        showShop();
    }

    private void showShop(){
        Table itemGrid = new Table();

        for (ShopItemData item : ShopItemData.values()) {

            Table card = new Table();

            Label title = new Label(item.getName(), skin);
            card.add(title).padTop(10).row();

            Image itemImage = new Image(textures.region(item.getImagePath()));
            card.add(itemImage).size(100, 100).pad(10).row();

            Label priceLabel = new Label(item.getPrice() + " " + item.getPaymentType(), skin);
            card.add(priceLabel).padBottom(5).row();

            TextButton buyBtn = new TextButton("BUY", skin);
            buyBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleItemPurchase(item);
                }
            });
            card.add(buyBtn).width(100).height(40).padBottom(10);

            itemGrid.add(card).pad(15);
        }

        ScrollPane scrollPane = new ScrollPane(itemGrid, skin);
        containerTable.add(scrollPane);

        addBackButton(() -> {
            App.setCurrentMenu(Menu.GAME_MENU);
            game.setScreen(new GameMenuScreen(game));
        });
    }

    private void handleItemPurchase(ShopItemData item) {
        if (item == ShopItemData.SEED_PACKET_BY_CHOICE) {
            showPlantSelectionDialog(item);
        } else {
            processPurchase(item, null);
        }
    }

    private void processPurchase(ShopItemData item, String selectedPlant){
        String result = ShopController.buyItem(item, selectedPlant);
        if(result.contains("ERROR:")){
            showErrorDialog(result);
        }else{
            showConfirmationDialog(item, result);
        }

    }

    private void showPlantSelectionDialog(ShopItemData item){
        containerTable.setVisible(false);
        Dialog plantDialog = new Dialog("Select a Plant", skin);
        List<String> plantList = new ArrayList<>();
        for (PlantData plant : PlantRepository.getInstance().getAllPlants()) {
            plantList.add(plant.getName());
        }
        String[] plantNames = plantList.toArray(new String[0]);

        SelectBox<String> plantSelect = new SelectBox<>(skin, "default");
        plantSelect.setItems(plantNames);
        plantDialog.getContentTable().add(new Label("Choose a plant for the seed packet:", skin)).pad(10).row();
        plantDialog.getContentTable().add(plantSelect).width(200).height(40).pad(10);

        TextButton confirmBtn = new TextButton("Confirm", skin, "default");
        TextButton cancelBtn = new TextButton("Cancel", skin, "default");

        confirmBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String selectedPlant = plantSelect.getSelected();
                processPurchase(item, selectedPlant);
                plantDialog.hide();
                containerTable.setVisible(true);
            }
        });

        cancelBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                plantDialog.hide();
                containerTable.setVisible(true);
            }
        });

        plantDialog.getButtonTable().add(confirmBtn).padRight(10);
        plantDialog.getButtonTable().add(cancelBtn);

        plantDialog.show(stage);
    }

    private void showConfirmationDialog(ShopItemData item, String chosenPlant) {
        Dialog confirmDialog = new Dialog("Confirm Purchase", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    String response = ShopController.addItemToProfile(item, chosenPlant, 1);
                    updateCurrency();
                    showErrorDialog(response);
                }
            }
        };
        confirmDialog.text("Are you sure you want to buy " + item.getName() + "?");
        confirmDialog.button("Yes", true);
        confirmDialog.button("No", false);
        confirmDialog.show(stage);
    }

    private void showErrorDialog(String error){
        Dialog dialog = new Dialog("Notice", skin) {
            @Override
            protected void result(Object object) {
            }
        };
        dialog.text(error);
        dialog.button("OK", true);
        dialog.show(stage);
    }
}
