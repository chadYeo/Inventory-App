package com.example.android.inventoryapp;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;

public class InventoryCursorAdapter extends CursorAdapter{

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameProduct = (TextView) view.findViewById(R.id.productName_textView);
        TextView priceProduct = (TextView) view.findViewById(R.id.price_textView);
        TextView qtyProduct = (TextView) view.findViewById(R.id.qty_textView);

        int name_columnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_NAME);
        int price_columnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRICE);
        int qty_columnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_QTY);

        String name = cursor.getString(name_columnIndex);
        String price = cursor.getString(price_columnIndex);
        String qty = cursor.getString(qty_columnIndex);

        nameProduct.setText(name);
        priceProduct.setText(price);
        qtyProduct.setText(qty);
    }
}
