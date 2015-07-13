package com.caci.inventory;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.caci.inventory.data.InventoryDbHelper;
import com.caci.inventory.data.PeopleDbHelper;
import com.caci.inventory.data.Person;

public class PersonActivity extends ActionBarActivity {
    private PeopleDbHelper peopleDbHelper;
    private Person person = null;
    private InventoryDbHelper inventoryDbHelper;
    private SimpleCursorAdapter dataAdapter;
    private static final String[] columns = new String[] {
            InventoryDbHelper.COLUMN_DESCRIPTION,
            InventoryDbHelper.COLUMN_BARCODE
    };
    private static final int[] to = new int[] {
            R.id.description,
            R.id.barcode
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        peopleDbHelper = new PeopleDbHelper(this);
        inventoryDbHelper = new InventoryDbHelper(this);

        final Button saveButton = (Button) findViewById(R.id.btnSave);
        final Button updateButton = (Button) findViewById(R.id.btnUpdate);
        final Button addInvButton = (Button) findViewById(R.id.btnAddInv);

        //Check to see if an id was passed.  If so, this will be an edit page, otherwise it will be a create
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            saveButton.setVisibility(View.GONE);
            updateButton.setVisibility(View.VISIBLE);

            int personId = extras.getInt("id");
            person = peopleDbHelper.getPerson(personId);

            ((EditText) findViewById(R.id.txtPersonName)).setText(person.getName(), TextView.BufferType.EDITABLE);
            ((EditText) findViewById(R.id.txtPersonLocation)).setText(person.getLocation(), TextView.BufferType.EDITABLE);
            ((EditText) findViewById(R.id.txtPersonRoom)).setText(person.getRoom(), TextView.BufferType.EDITABLE);

            updateButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    person.setName(((EditText) findViewById(R.id.txtPersonName)).getText().toString());
                    person.setLocation(((EditText) findViewById(R.id.txtPersonLocation)).getText().toString());
                    person.setRoom(((EditText) findViewById(R.id.txtPersonRoom)).getText().toString());
                    peopleDbHelper.updatePerson(person);
                    Toast.makeText(getBaseContext(), "Person Updated", Toast.LENGTH_LONG).show();
                }
            });

            showInventory();
        } else {
            saveButton.setVisibility(View.VISIBLE);
            updateButton.setVisibility(View.GONE);
            addInvButton.setVisibility(View.GONE);
            findViewById(R.id.listInventory).setVisibility(View.GONE);

            //Save btn visible if this is a new person only
            saveButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Person p = new Person();
                    p.setName(((EditText) findViewById(R.id.txtPersonName)).getText().toString());
                    p.setLocation(((EditText) findViewById(R.id.txtPersonLocation)).getText().toString());
                    p.setRoom(((EditText) findViewById(R.id.txtPersonRoom)).getText().toString());
                    //Save new person to the db and set the local object to the newly created record
                    person = peopleDbHelper.createPerson(p);

                    Toast.makeText(getBaseContext(), "Person Saved", Toast.LENGTH_LONG).show();
                    saveButton.setVisibility(View.GONE);
                    updateButton.setVisibility(View.VISIBLE);
                    showInventory();
                }
            });
        }

    }

    private void showInventory() {
        final Button addInvButton = (Button) findViewById(R.id.btnAddInv);
        addInvButton.setVisibility(View.VISIBLE);
        addInvButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PersonActivity.this, InventoryActivity.class);
                intent.putExtra("personid", person.getId());
                startActivity(intent);
            }
        });
        findViewById(R.id.listInventory).setVisibility(View.VISIBLE);
        Cursor cursor = inventoryDbHelper.getAllInventoryCursor(person.getId());
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.inventory_info,
                cursor,
                columns,
                to,
                0);
        ListView listView = (ListView) findViewById(R.id.listInventory);
        listView.setAdapter(dataAdapter);
        TextView inventoryHeaderTextView = new TextView(getApplicationContext());
        inventoryHeaderTextView.setText("Inventory");
        listView.addHeaderView(inventoryHeaderTextView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                Intent intent = new Intent(PersonActivity.this, InventoryActivity.class);
                intent.putExtra("personid", person.getId());
                intent.putExtra("id", cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (person!=null) {
            Cursor cursor = inventoryDbHelper.getAllInventoryCursor(person.getId());
            dataAdapter.swapCursor(cursor);
            dataAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //Handle delete, unless it's a new person that hasn't been saved yet
        if (id == R.id.action_delete) {
            if (person!=null) {
                peopleDbHelper.deletePerson(person);
                inventoryDbHelper.deleteInventoryItemsByPerson(person.getId());
                Toast.makeText(getBaseContext(), "Person Deleted", Toast.LENGTH_LONG).show();
                this.finish();
            } else return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
