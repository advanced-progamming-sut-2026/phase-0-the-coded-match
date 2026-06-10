package Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Shop {
    List<ShopItem> storeShelves = new ArrayList<>();

    private ShopItem dailyItem;
    private LocalDate lastRefreshDate;
    private boolean hasPurchasedToday;

    public Shop(){
        initializePermanentItems();
        this.lastRefreshDate = LocalDate.MIN;
        this.hasPurchasedToday = false;
    }

    private void checkAndRefreshDailyOffer(){

    }

    private void initializePermanentItems(){

    }

}
