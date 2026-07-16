package controllers;

import enums.Commands;
import enums.PlantCategory;
import enums.ShopRelated.PaymentType;
import enums.ShopRelated.ShopItemData;
import models.App;
import models.DroppedSeedPacket;
import models.Shop;
import models.greenhouse.GreenHouse;
import models.greenhouse.GreenHousePot;
import models.plants.Plant;

import javax.swing.*;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShopController {
    private static Shop shop;

    public ShopController(Shop shop) {
        this.shop = shop;
    }

    public static StringBuilder showShopList(StringBuilder sb) {
        for (ShopItemData shopItem : ShopItemData.values()) {
            if (shopItem == ShopItemData.SEED_PACKET_DAILY) {
                continue;
            }
            sb.append("id: ").append(shopItem.getId()).append(" - item name: ").append(shopItem.getName()).append("\n");
            if (shopItem == ShopItemData.SEED_PACKET_BY_CHANCE) {
                sb.append("plant: ").append(shop.getRandomSeedPack().getData().getName()).append("\n");
            }
            sb.append("price: ").append(shopItem.getPrice()).append(shopItem.getPaymentType().getName())
                    .append(" - buying unit: ").append(shopItem.getUnitBought()).append("\n");
        }
        return sb;
    }

    public static StringBuilder showDailyShop(StringBuilder sb) {
        ShopItemData dailyItem = ShopItemData.SEED_PACKET_DAILY;
        sb.append("id: ").append(dailyItem.getId()).append(" - item name: ").append(dailyItem.getName()).append("\n");
        sb.append("plant: ").append(shop.getRandomSpecialSeedPack().getData().getName()).append("\n");
        sb.append("price: ").append(dailyItem.getPrice()).append(dailyItem.getPaymentType().getName())
                .append(" - buying unit: ").append(dailyItem.getUnitBought()).append("\n");
        return sb;
    }

    public static String buyItem(String input) {
        Pattern pattern = Pattern.compile(Commands.SHOP_BUY.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return "invalid command";
        }
        int id = Integer.parseInt(matcher.group("item_id"));
        ShopItemData item = getItemById(id);
        int count = Integer.parseInt(matcher.group("count"));

        if (item == null) {
            return "invalid item id";
        }

        Plant plantChosen = null;

        if (item == ShopItemData.SEED_PACKET_BY_CHOICE) {
            String plantChosenSt = matcher.group(3);
            plantChosen = App.getPlantByName(plantChosenSt);
            if (plantChosen == null) {
                return "invalid plant name";
            }
        } else if (item == ShopItemData.SEED_PACKET_DAILY && shop.isDailyItemSoldOut()) {
            return "daily item has been sold out, come back tomorrow";
        }

        if (item.getPaymentType() == PaymentType.COIN) {
            if (item.getPrice() > App.getCurrentUser().getCoinsCount()) {
                return "not enough coins to buy this item";
            } else {
                App.getCurrentUser().setCoinsCount(App.getCurrentUser().getCoinsCount() - item.getPrice());
                addItemToProfile(item, plantChosen, count);
            }
        } else {
            if (item.getPrice() > App.getCurrentUser().getGemsCount()) {
                return "not enough gems to buy this item";
            } else {
                App.getCurrentUser().setGemsCount(App.getCurrentUser().getGemsCount() - item.getPrice());
                addItemToProfile(item, plantChosen, count);
            }
        }
        return "";
    }

    public Plant getRandomPlant(){
        List<Plant> unlockedPlants = App.getCurrentUser().getCollection().getAvailablePlants();
        int randomIndex = new Random().nextInt(unlockedPlants.size());
        return unlockedPlants.get(randomIndex);
    }

    public static ShopItemData getItemById(int id) {
        for (ShopItemData itemData : ShopItemData.values()) {
            if (itemData.getId() == id) {
                return itemData;
            }
        }
        return null;
    }

    public static String addItemToProfile(ShopItemData item, Plant plant, int count) {
        switch (item) {
            case POT -> {
                if (GreenHouse.getPotsCount() + count > 20) {
                    return "greenhouse is full";
                }
                for (int i = 0; i < count; i++) {
                    GreenHousePot pot = new GreenHousePot();//todo: where to put the pot?
                }
                return count + " pot(s) bought successfully";
            }
            case PLANT_FOOD -> {
                if (App.getCurrentUser().getPlantFoodBoughtCount() + count > 3) {
                    return "plant food count is maximum";
                }
                App.getCurrentUser().setPlantFoodBoughtCount(App.getCurrentUser().getPlantFoodBoughtCount() +
                        (count * item.getUnitBought()));
                return count + " plant food(s) bought successfully";
            }
            case SEED_PACKET_BY_CHANCE -> {
                DroppedSeedPacket seedPacket = new DroppedSeedPacket(false, shop.getRandomSeedPack());
                //todo: add the seed packet
            }
            case SEED_PACKET_BY_CHOICE -> {
                DroppedSeedPacket seedPacket = new DroppedSeedPacket(false, plant);
            }
            case EXCHANGE_CURRENCY -> {
                int amount = count * item.getUnitBought();
                App.getCurrentUser().setCoinsCount(App.getCurrentUser().getCoinsCount() + amount);
                return amount + " coins were exchanged with 5 gems";
            }
            case SEED_PACKET_DAILY -> {
                DroppedSeedPacket seedPacket = new DroppedSeedPacket(false, shop.getRandomSpecialSeedPack());
                shop.setDailyItemSoldOut(true);
            }
        }
        return "";
    }
}
