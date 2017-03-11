package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOGT_TAG = EditActivity.class.getSimpleName();

    // this is the action code we use in our intent,
    // this way we know we're looking at the response from our own action
    public static final int RESULT_LOAD_IMAGE = 0;

    private static final int EXISTING_ITEM_LOADER = 0;

    private Uri mCurrentUri;

    private EditText mItemName;
    private EditText mPrice;
    private EditText mQty;

    private ImageButton mQtyInc_button;
    private ImageButton mQtyDec_button;

    private Button mSelectImageButton;
    private Button mOrderButton;

    private ImageView mImageView;
    private Uri mImageUri;

    private boolean mItemhasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mItemhasChanged = true;
            return false;
        }
    };

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
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        mItemName = (EditText) findViewById(R.id.nameOfProduct_editText);
        mPrice = (EditText) findViewById(R.id.price_editText);
        mQty = (EditText) findViewById(R.id.qty_editText);
        mQty.setText("0", TextView.BufferType.EDITABLE);

        mQtyInc_button = (ImageButton) findViewById(R.id.qtyInc_imageButton);
        mQtyInc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(mQty.getText().toString());
                mQty.setText(String.valueOf(qty + 1));
            }
        });

        mQtyDec_button = (ImageButton) findViewById(R.id.qtyDec_imageButton);
        mQtyDec_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(mQty.getText().toString());
                mQty.setText(String.valueOf(qty-1));
            }
        });

        mSelectImageButton = (Button) findViewById(R.id.select_image_button);
        mSelectImageButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(imageIntent, RESULT_LOAD_IMAGE);
            }
        });

        mImageView = (ImageView) findViewById(R.id.edit_imageView);

        mOrderButton = (Button) findViewById(R.id.order_button);
        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, "order@order.com");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Requesting for an order");

                String name = mItemName.getText().toString();
                String qty = mQty.getText().toString();

                emailIntent.putExtra(Intent.EXTRA_TEXT, "I need to order more " + name + "\n" + "I have only " + qty + " in stock");
                emailIntent.setType("text/plain");
                startActivity(emailIntent);
            }
        });

        mItemName.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mQty.setOnTouchListener(mTouchListener);
        mQtyInc_button.setOnTouchListener(mTouchListener);
        mQtyDec_button.setOnTouchListener(mTouchListener);
        mSelectImageButton.setOnTouchListener(mTouchListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            mImageUri = Uri.parse(selectedImage.toString());
            mImageView.setImageURI(selectedImage);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mItemhasChanged) {
            super.onBackPressed();
            return;
        }else {
            DialogInterface.OnClickListener disregardButtonClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    finish();
                }
            };
            showUnsavedChangesDialog(disregardButtonClickListener);
        }
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
                if (!mItemhasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener disregardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditActivity.this);
                    }
                };
                showUnsavedChangesDialog(disregardButtonClickListener);
                return true;
            case R.id.save:
                addInfoEntered();
                return true;
            case R.id.delete_data:
                showDeleteDialog();
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

        return new CursorLoader(this, mCurrentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int imageColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_IMAGE);
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRICE);
            int qtyColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_QTY);

            String imageUri = cursor.getString(imageColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int qty = cursor.getInt(qtyColumnIndex);

            if (mImageUri != null) {
                mImageView.setImageURI(Uri.parse(imageUri));
            }
            mItemName.setText(name);
            mPrice.setText(price);
            mQty.setText(Integer.toString(qty));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mImageView.setImageDrawable(null);
        mItemName.setText("");
        mPrice.setText("");
        mQty.setText("");
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_dialog_message);
        builder.setPositiveButton(R.string.disregard, discardButtonClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        if (mCurrentUri != null) {
            int mRowsDeleted = getContentResolver().delete(mCurrentUri, null, null);

            if (mRowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_item_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_item_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void addInfoEntered() {
        String itemString = mItemName.getText().toString().trim();
        String priceString = mPrice.getText().toString().trim();
        String qtyString = mQty.getText().toString().trim();

        if (TextUtils.isEmpty(itemString) || TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, R.string.editor_empty_info, Toast.LENGTH_SHORT).show();
        } else {
            ContentValues values = new ContentValues();
            if (mImageUri != null) {
                values.put(ItemEntry.COLUMN_IMAGE, mImageUri.toString());
            };
            values.put(ItemEntry.COLUMN_NAME, itemString);
            values.put(ItemEntry.COLUMN_PRICE, priceString);
            values.put(ItemEntry.COLUMN_QTY, qtyString);

            if (mCurrentUri == null) {
                Uri uri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);

                if (uri == null) {
                    Toast.makeText(this, "Error with saving Item", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Item Saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                int rowAffected = getContentResolver().update(mCurrentUri, values, null, null);

                if (rowAffected == 0) {
                    Toast.makeText(this, getString(R.string.editor_update_item_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_update_item_successful), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}
