package com.example.android.inventoryapp;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final int MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE = 123;
    private static final int INVENTORY_LOADER = 0;

    InventoryCursorAdapter mCursorAdapter;
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing for now
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        ListView listView = (ListView) findViewById(R.id.listView);
        View emptyView = findViewById(R.id.empty_textView);
        listView.setEmptyView(emptyView);

        mCursorAdapter = new InventoryCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);

                Uri currentUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);

                intent.setData(currentUri);

                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    private void insertItem() {
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_NAME, "Test HeadPhone");
        values.put(ItemEntry.COLUMN_PRICE, "100");
        values.put(ItemEntry.COLUMN_QTY, "100");

        Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
    }

    public void deleteAllItems() {
        int mRowsDeleted = getContentResolver().delete(ItemEntry.CONTENT_URI, null, null);

        if (mRowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.editor_delete_item_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_delete_item_successful), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_dummyData:
                insertItem();
                return true;
            case R.id.delete_AllData:
                deleteAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_IMAGE,
                ItemEntry.COLUMN_NAME,
                ItemEntry.COLUMN_PRICE,
                ItemEntry.COLUMN_QTY
        };

        return new CursorLoader(this, ItemEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
