package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Inventory.db";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + ItemEntry.TABLE_NAME + " ("
                        + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + ItemEntry.COLUMN_NAME + " TEXT NOT NULL, "
                        + ItemEntry.COLUMN_PRICE + " TEXT NOT NULL DEFAULT 0, "
                        + ItemEntry.COLUMN_QTY + " Text NOT NULL DEFAULT 0, "
                        + ItemEntry.COLUMN_IMAGE + " Text);";

        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
