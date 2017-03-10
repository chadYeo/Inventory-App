package com.example.android.inventoryapp;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;

public class InventoryCursorAdapter extends CursorAdapter{

    private Context mContext;

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, Context context, Cursor cursor) {

        mContext = context;

        TextView nameProduct = (TextView) view.findViewById(R.id.productName_textView);
        TextView priceProduct = (TextView) view.findViewById(R.id.price_textView);
        final TextView qtyProduct = (TextView) view.findViewById(R.id.qty_count_textView);

        int name_columnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_NAME);
        int price_columnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRICE);
        int qty_columnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_QTY);

        final String name = cursor.getString(name_columnIndex);
        final Float price = cursor.getFloat(price_columnIndex);
        final Integer qty = cursor.getInt(qty_columnIndex);

        nameProduct.setText(name);
        priceProduct.setText(Float.toString(price));
        qtyProduct.setText(Integer.toString(qty));

        Button mSoldButton = (Button) view.findViewById(R.id.sold_button);
        mSoldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    Object ojb = v.getTag();
                    String st = ojb.toString();
                    ContentValues values = new ContentValues();
                    values.put(ItemEntry.COLUMN_NAME, name);
                    values.put(ItemEntry.COLUMN_PRICE, price);
                    values.put(ItemEntry.COLUMN_QTY, qty >= 1? qty-1: 0);

                    Uri currentUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, Integer.parseInt(st));

                    int rowsAffected = mContext.getContentResolver().update(currentUri, values, null, null);
                    if (rowsAffected == 0 || qty == 0) {
                        Toast.makeText(mContext, "Error selling item", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        Object obj = cursor.getInt(cursor.getColumnIndex(ItemEntry._ID));
        mSoldButton.setTag(obj);
    }
}
