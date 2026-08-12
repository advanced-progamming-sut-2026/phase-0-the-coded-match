package PvZ2.APproject.views;

import PvZ2.APproject.controllers.ShopController;
import PvZ2.APproject.enums.Commands;

public class ShopView {
    public static StringBuilder stringBuilder = new StringBuilder();

    public static void check(String input) {
        if (input.matches(Commands.SHOP_LIST.getPattern())) {
            ShopController.showShopList(stringBuilder);
            System.out.println(stringBuilder.toString());
            stringBuilder.delete(0, stringBuilder.length());
        } else if (input.matches(Commands.SHOP_DAILY.getPattern())) {
            ShopController.showDailyShop(stringBuilder);
            System.out.println(stringBuilder.toString());
            stringBuilder.delete(0, stringBuilder.length());
        } else if (input.matches(Commands.SHOP_BUY.getPattern())) {
            System.out.println(ShopController.buyItem(input));
        } else {
            System.out.println("invalid command");
        }
    }
}
