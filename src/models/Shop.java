package models;

import enums.ShopItemData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Shop {
    List<ShopItemData> storeShelves = new ArrayList<>();

    public Shop(){
        initializePermanentItems();
    }

    private void checkAndRefreshDailyOffer(){

    }

    private void initializePermanentItems(){

    }

}
