package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EXISTING_PET_LAODER = 0;

    private Uri mCurrentUri;

    private EditText mItemName;
    private EditText mPrice;
    private EditText mQty;
    private Button mSoldButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        if (mCurrentUri == null) {
            setTitle(getString(R.string.editor_title_new_item));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_title_edit_item));
            getLoaderManager().initLoader(EXISTING_PET_LAODER, null, this);
        }

        mItemName = (EditText) findViewById(R.id.nameOfProduct_editText);
        mPrice = (EditText) findViewById(R.id.price_editText);
        mQty = (EditText) findViewById(R.id.qty_editText);
        mSoldButton = (Button) findViewById(R.id.sold_button);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_data);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.save:
                addInfoEntered();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addInfoEntered() {
        String itemString = mItemName.getText().toString().trim();
        String priceString = mPrice.getText().toString().trim();
        String qtyString = mQty.getText().toString().trim();

        if (mCurrentUri == null &&
                TextUtils.isEmpty(itemString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(qtyString)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_NAME, itemString);
        values.put(ItemEntry.COLUMN_PRICE, priceString);
        values.put(ItemEntry.COLUMN_QTY, qtyString);

        if (mCurrentUri == null) {
            Uri uri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);

            if (uri == null) {
                Toast.makeText(this, "Error with saving Item", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item Saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowAffected = getContentResolver().update(mCurrentUri, values, null, null);

            if (rowAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_item_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_item_successful), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_NAME,
                ItemEntry.COLUMN_PRICE,
                ItemEntry.COLUMN_QTY
        };

        return new CursorLoader(this, mCurrentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRICE);
            int qtyColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_QTY);

            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int qty = cursor.getInt(qtyColumnIndex);

            mItemName.setText(name);
            mPrice.setText(price);
            mQty.setText(Integer.toString(qty));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemName.setText("");
        mPrice.setText("");
        mQty.setText("");
    }
}
