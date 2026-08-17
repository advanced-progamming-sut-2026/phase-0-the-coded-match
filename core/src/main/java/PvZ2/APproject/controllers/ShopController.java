package PvZ2.APproject.controllers;

import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.ShopRelated.PaymentType;
import PvZ2.APproject.enums.ShopRelated.ShopItemData;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.Shop;
import PvZ2.APproject.models.greenhouse.GreenHouse;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShopController {

    public static void checkAndRefreshDailyOffer() {
        Shop shop = App.getCurrentUser().getShop();
        if (shop == null) return;
        String todayDate = LocalDate.now().toString();
        if (shop.getLastUpdateDate() == null || !shop.getLastUpdateDate().equals(todayDate)) {
            shop.setRandomSeedPack(getRandomPlant());
            shop.setRandomSpecialSeedPack(getRandomPlant());
            shop.setDailyItemSoldOut(false);
            shop.setLastUpdateDate(todayDate);
        }
    }

    public static StringBuilder showShopList(StringBuilder sb) {
        checkAndRefreshDailyOffer();
        Shop shop = App.getCurrentUser().getShop();
        for (ShopItemData shopItem : ShopItemData.values()) {
            if (shopItem == ShopItemData.SEED_PACKET_DAILY) {
                continue;
            }
            sb.append("id: ").append(shopItem.getId()).append(" - item name: ").append(shopItem.getName()).append("\n");
            if (shopItem == ShopItemData.SEED_PACKET_BY_CHANCE) {
                sb.append("plant: ").append(shop.getRandomSeedPack().getName()).append("\n");
            }
            sb.append("price: ").append(shopItem.getPrice()).append(shopItem.getPaymentType().getName())
                    .append(" - buying unit: ").append(shopItem.getUnitBought()).append("\n");
        }
        return sb;
    }

    public static StringBuilder showDailyShop(StringBuilder sb) {
        checkAndRefreshDailyOffer();
        Shop shop = App.getCurrentUser().getShop();
        ShopItemData dailyItem = ShopItemData.SEED_PACKET_DAILY;
        sb.append("id: ").append(dailyItem.getId()).append(" - item name: ").append(dailyItem.getName()).append("\n");
        sb.append("plant: ").append(shop.getRandomSpecialSeedPack().getName()).append("\n");
        sb.append("price: ").append(dailyItem.getPrice()).append(dailyItem.getPaymentType().getName())
                .append(" - buying unit: ").append(dailyItem.getUnitBought()).append("\n");
        return sb;
    }

    public static String buyItem(ShopItemData item, String plantSelected) {
        Shop shop = App.getCurrentUser().getShop();
//        Pattern pattern = Pattern.compile(Commands.SHOP_BUY.getPattern());
//        Matcher matcher = pattern.matcher(input);
//        if (!matcher.matches()) {
//            return "invalid command";
//        }
//        int id = Integer.parseInt(matcher.group("itemId"));
//        ShopItemData item = getItemById(id);
//        int count = Integer.parseInt(matcher.group("count"));
//        if (count < 1) return "invalid count";
//
//        if (item == null) {
//            return "invalid item id";
//        }
        PlantData plantChosen = null;
        if (item == ShopItemData.SEED_PACKET_DAILY && shop.isDailyItemSoldOut()) return "ERROR: daily item has been sold out, come back tomorrow";

        if (item == ShopItemData.SEED_PACKET_BY_CHANCE) plantChosen = shop.getRandomSeedPack();
        else if (item == ShopItemData.SEED_PACKET_DAILY) plantChosen = shop.getRandomSpecialSeedPack();
        else if (item == ShopItemData.SEED_PACKET_BY_CHOICE) {
            plantChosen = PlantRepository.getInstance().findByName(plantSelected);
            if (plantChosen == null || !App.getCurrentUser().getCollection().getAvailablePlantsIds().contains(plantChosen.getId())) {
                return "ERROR: you don't have access to plant";
            }
        }

        if (item == ShopItemData.POT && App.getCurrentUser().getGreenHouse().getPotsCount() + 1 > 20) return "ERROR: greenhouse is full";
        if (item == ShopItemData.PLANT_FOOD && App.getCurrentUser().getPlantFoodBoughtCount() + 1 > 3) return "ERROR: plant food count is at maximum capacity";
        int price = item.getPrice() * 1;
        if (item.getPaymentType() == PaymentType.COIN) {
            if (price > App.getCurrentUser().getCoinsCount()) {
                return "ERROR: not enough coins to buy this item";
            } else {
                App.getCurrentUser().setCoinsCount(App.getCurrentUser().getCoinsCount() - price);
            }
        } else {
            if (price > App.getCurrentUser().getGemsCount()) {
                return "ERROR: not enough gems to buy this item";
            } else {
                App.getCurrentUser().setGemsCount(App.getCurrentUser().getGemsCount() - price);
            }
        }
//        return addItemToProfile(item, plantChosen, 1);
        return plantChosen.getName();
    }

    public static PlantData getRandomPlant(){
        List<String> unlockedPlants = App.getCurrentUser().getCollection().getAvailablePlantsIds();
        if (unlockedPlants.isEmpty()) return PlantRepository.getInstance().getAllPlants().get(0);
        int randomIndex = new Random().nextInt(unlockedPlants.size());
        return PlantRepository.getInstance().findById(unlockedPlants.get(randomIndex));
    }

    public static ShopItemData getItemById(int id) {
        for (ShopItemData itemData : ShopItemData.values()) {
            if (itemData.getId() == id) {
                return itemData;
            }
        }
        return null;
    }

    public static String addItemToProfile(ShopItemData item, String plantChosen, int count) {
        PlantData plant = PlantRepository.getInstance().findByName(plantChosen);
        Shop shop = App.getCurrentUser().getShop();
        switch (item) {
            case POT -> {
                GreenHouse greenHouse = App.getCurrentUser().getGreenHouse();
                if (greenHouse.getPotsCount() + count > 20) {
                    return "ERROR: greenhouse is full";
                }
                int unlocked = greenHouse.unlockPots(count);
                return unlocked + " pot bought successfully";
            }
            case PLANT_FOOD -> {
                if (App.getCurrentUser().getPlantFoodBoughtCount() + count > 3) {
                    return "ERROR: plant food count is maximum";
                }
                App.getCurrentUser().setPlantFoodBoughtCount(App.getCurrentUser().getPlantFoodBoughtCount() +
                        (count * item.getUnitBought()));
                return count + " plant food(s) bought successfully";
            }
            case SEED_PACKET_BY_CHANCE, SEED_PACKET_BY_CHOICE -> {
                int amount = count * item.getUnitBought();
                App.getCurrentUser().addSeedPackets(plant.getName(), amount);
                return amount + " seed packets bought successfully";
            }
            case EXCHANGE_CURRENCY -> {
                int amount = count * item.getUnitBought();
                App.getCurrentUser().setCoinsCount(App.getCurrentUser().getCoinsCount() + amount);
                return amount + " coins were exchanged with 5 gems";
            }
            case SEED_PACKET_DAILY -> {
                int amount = count * item.getUnitBought();
                App.getCurrentUser().addSeedPackets(plant.getName(), amount);
                shop.setDailyItemSoldOut(true);
                return amount + " daily seed packets bought successfully";
            }
        }
        return "";
    }
}
