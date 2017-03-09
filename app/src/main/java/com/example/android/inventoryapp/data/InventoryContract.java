package com.example.android.inventoryapp.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private InventoryContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_INVENTORY = "inventory";

    public static class ItemEntry implements BaseColumns {
        public static final String _ID = BaseColumns._ID;
        public static final String TABLE_NAME = "Inventory";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QTY = "qty";

        public static final Uri CONTENT_URI  = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        // Custom MIME types
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

    }

}
