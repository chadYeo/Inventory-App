package com.example.android.inventoryapp.data;


import android.provider.BaseColumns;

public class InventoryContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private InventoryContract() {}

    public static class ItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "Inventory";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QTY = "qty";
    }

}
