package com.caci.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.caci.inventory.data.InventoryDbHelper;
import com.caci.inventory.data.InventoryItem;


public class InventoryActivity extends ActionBarActivity {
    private InventoryDbHelper inventoryDbHelper;
    private InventoryItem inventoryItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        inventoryDbHelper = new InventoryDbHelper(this);

        final Button saveButton = (Button) findViewById(R.id.btnSave);
        final Button updateButton = (Button) findViewById(R.id.btnUpdateInv);
        final Button scanButton = (Button) findViewById(R.id.btnScan);

        //Check to see if an id was passed.  If so, this will be an edit page, otherwise it will be a create
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final int personId = extras.getInt("personid");
            int inventoryId = extras.getInt("id", -1);
            if (inventoryId != -1) {
                saveButton.setVisibility(View.GONE);
                updateButton.setVisibility(View.VISIBLE);

                inventoryItem = inventoryDbHelper.getInventoryItem(inventoryId);

                ((EditText) findViewById(R.id.txtInventoryDesc)).setText(inventoryItem.getDescription(), TextView.BufferType.EDITABLE);
                ((EditText) findViewById(R.id.txtInvBarcode)).setText(inventoryItem.getBarcode(), TextView.BufferType.EDITABLE);

                updateButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        inventoryItem.setDescription(((EditText) findViewById(R.id.txtInventoryDesc)).getText().toString());
                        inventoryItem.setBarcode(((EditText) findViewById(R.id.txtInvBarcode)).getText().toString());
                        inventoryDbHelper.updateInventoryItem(inventoryItem);
                        Toast.makeText(getBaseContext(), "Inventory Item Updated", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                saveButton.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.GONE);

                //Save btn visible if this is a new person only
                saveButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        InventoryItem item = new InventoryItem();
                        item.setPersonId(personId);
                        item.setDescription(((EditText) findViewById(R.id.txtInventoryDesc)).getText().toString());
                        item.setBarcode(((EditText) findViewById(R.id.txtInvBarcode)).getText().toString());
                        //Save new item to the db and set the local object to the newly created record
                        inventoryItem = inventoryDbHelper.createInventoryItem(item);

                        Toast.makeText(getBaseContext(), "Inventory Item Saved", Toast.LENGTH_LONG).show();
                        saveButton.setVisibility(View.GONE);
                        updateButton.setVisibility(View.VISIBLE);

                        finish();
                    }
                });
            }
        }

        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(InventoryActivity.this, ZbarScanActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            ((EditText) findViewById(R.id.txtInvBarcode)).setText(data.getStringExtra("barcode"), TextView.BufferType.EDITABLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            if (inventoryItem!=null) {
                inventoryDbHelper.deleteInventoryItem(inventoryItem);
                Toast.makeText(getBaseContext(), "Inventory Item Deleted", Toast.LENGTH_LONG).show();
                this.finish();
            } else return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
